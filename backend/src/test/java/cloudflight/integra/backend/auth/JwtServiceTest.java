package cloudflight.integra.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link JwtService}.
 *
 * <p>We use {@code @SpringBootTest} with a small footprint — the full context is
 * loaded so that {@code @Value} injection from {@code application.yaml} works
 * correctly. Alternatively, we could use {@code ReflectionTestUtils} to inject
 * values directly, but using the real config keeps the test realistic.</p>
 */
@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_thenExtractUsername_shouldMatch() {
        String username = "alice";

        String token = jwtService.generateToken(username);

        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    void validToken_shouldPassValidation() {
        String token = jwtService.generateToken("bob");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void expiredToken_shouldFailValidation() {
        // Create a JwtService with 0 ms expiry so the token expires immediately
        JwtService shortLivedService = new JwtService(
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
            0L
        );

        String token = shortLivedService.generateToken("charlie");

        // Token should already be expired
        assertThat(shortLivedService.isTokenValid(token)).isFalse();
    }

    @Test
    void tamperedToken_shouldFailValidation() {
        String token = jwtService.generateToken("dave");
        // Flip one character in the signature section to simulate tampering
        String tampered = token.substring(0, token.length() - 3) + "XXX";

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}
