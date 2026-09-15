package lk.ijse.jewellery_management_system.entity;

import jakarta.persistence.*;
import lk.ijse.jewellery_management_system.enumeration.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * User entity that also serves as the Spring Security principal.
 * By implementing UserDetails, Spring Security can load this directly
 * from the database and verify passwords, roles, and account status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, STAFF, CUSTOMER

    // ── UserDetails contract ──────────────────────────────────────────────────

    /**
     * Maps the Role enum to a GrantedAuthority so Spring Security
     * can evaluate .hasRole("ADMIN") / .hasRole("STAFF") etc.
     * Spring prefixes "ROLE_" automatically when using hasRole(),
     * so we store the plain enum name here.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String  getPassword()             { return password; }
    @Override public String  getUsername()             { return username; }

    // All account status flags return true — no account locking logic yet
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
