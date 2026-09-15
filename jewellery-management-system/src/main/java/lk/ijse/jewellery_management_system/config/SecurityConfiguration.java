package lk.ijse.jewellery_management_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider  authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Disable CSRF (not needed for stateless JWT APIs)
            .csrf(AbstractHttpConfigurer::disable)

            // Role-Based Access Control
            .authorizeHttpRequests(auth -> auth

                // Public endpoints (no token required)
                .requestMatchers(
                        "/api/v1/auth/**",   // register + authenticate
                        "/*.html",           // root HTML files
                        "/css/**",           // stylesheets
                        "/js/**",            // scripts
                        "/assets/**"         // images / fonts
                ).permitAll()

                // ADMIN only: gold rate management
                .requestMatchers("/api/v1/gold-rates/**").hasRole("ADMIN")

                // ADMIN + STAFF: inventory / stock operations
                .requestMatchers("/api/v1/inventory/**").hasAnyRole("ADMIN", "STAFF")

                // STAFF only: order placement and management
                .requestMatchers("/api/v1/orders/**").hasRole("STAFF")

                // STAFF + CUSTOMER: repair tracking
                .requestMatchers("/api/v1/repairs/**").hasAnyRole("STAFF", "CUSTOMER")

                // Everything else: must be authenticated
                .anyRequest().authenticated()
            )

            // Stateless session — no HTTP session will be created
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Wire our DaoAuthenticationProvider
            .authenticationProvider(authenticationProvider)

            // Run JWT filter BEFORE Spring's own username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}