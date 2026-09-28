package cloudflight.integra.backend.auth.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * JPA entity representing an application user.
 * The password field stores the BCrypt hash — never the plaintext.
 */
@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Always stored as a BCrypt hash. Plain-text passwords are NEVER persisted.
     */
    @Column(nullable = false)
    private String password;

    protected AppUser() {
        // required by JPA
    }

    public AppUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
