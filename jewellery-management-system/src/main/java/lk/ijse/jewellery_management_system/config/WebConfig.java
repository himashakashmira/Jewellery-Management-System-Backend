package lk.ijse.jewellery_management_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allow all endpoints in the backend
        registry.addMapping("/**")
                // allow VS Code Live Server address
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
                // allow all HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}