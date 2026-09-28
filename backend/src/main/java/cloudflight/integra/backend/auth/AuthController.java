package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.auth.model.LoginRequestDto;
import cloudflight.integra.backend.auth.model.LoginResponse;
import cloudflight.integra.backend.auth.model.RegisterRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequestDto dto) {
        authService.register(dto.username(), dto.password());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto.username(), dto.password());
    }
}
