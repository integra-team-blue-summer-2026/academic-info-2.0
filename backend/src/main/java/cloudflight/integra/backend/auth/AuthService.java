package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.auth.model.AppUser;
import cloudflight.integra.backend.auth.model.LoginRequestDto;
import cloudflight.integra.backend.auth.model.LoginResponse;
import cloudflight.integra.backend.auth.model.RegisterRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;


@Service
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern DIGIT     = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL   = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");


    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public void register(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        if (repository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }

        String hashedPassword = passwordEncoder.encode(password);
        repository.save(new AppUser(username, hashedPassword));
    }


    public LoginResponse login(String username, String password) {
        AppUser user = repository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(token);
    }


    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username must not be blank");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long"
            );
        }
        if (!UPPERCASE.matcher(password).find()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must contain at least one uppercase letter"
            );
        }
        if (!LOWERCASE.matcher(password).find()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must contain at least one lowercase letter"
            );
        }
        if (!DIGIT.matcher(password).find()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must contain at least one digit"
            );
        }
        if (!SPECIAL.matcher(password).find()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Password must contain at least one special character"
            );
        }
    }
}
