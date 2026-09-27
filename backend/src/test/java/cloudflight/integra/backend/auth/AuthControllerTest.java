package cloudflight.integra.backend.auth;

import cloudflight.integra.backend.auth.model.LoginRequestDto;
import cloudflight.integra.backend.auth.model.RegisterRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the authentication flow.
 *
 * <p>We use {@code @SpringBootTest + @AutoConfigureMockMvc} (full context) instead
 * of {@code @WebMvcTest} because we want to exercise the complete security filter
 * chain, not just the controller layer in isolation.</p>
 *
 * <p>H2 in-memory DB is used automatically — each test class gets a fresh database
 * because {@code ddl-auto: create-drop} drops and recreates the schema on startup.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository userRepository;

    private static final String VALID_PASSWORD = "Password1!";
    private static final String USERNAME = "testuser";

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    @Test
    void register_withValidCredentials_returns201() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isCreated());
    }

    @Test
    void register_withDuplicateUsername_returns409() throws Exception {
        // Register once
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isCreated());

        // Try to register again with the same username
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isConflict());
    }

    @Test
    void register_withShortPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, "Ab1!"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withoutUppercase_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, "password1!"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withoutLowercase_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, "PASSWORD1!"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withoutDigit_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, "Password!"))))
            .andExpect(status().isBadRequest());
    }

    @Test
    void register_withoutSpecialChar_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, "Password1"))))
            .andExpect(status().isBadRequest());
    }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    @Test
    void login_withValidCredentials_returns200WithToken() throws Exception {
        // Register first
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isCreated());

        // Then login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequestDto(USERNAME, "WrongPass1!"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownUser_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequestDto("nobody", VALID_PASSWORD))))
            .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Protected route access
    // -------------------------------------------------------------------------

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withValidToken_returns200() throws Exception {
        // Register + login to get a token
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new RegisterRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(new LoginRequestDto(USERNAME, VALID_PASSWORD))))
            .andExpect(status().isOk())
            .andReturn();

        String token = objectMapper
            .readTree(loginResult.getResponse().getContentAsString())
            .get("token")
            .asText();

        assertThat(token).isNotBlank();

        // Access protected endpoint with the token
        mockMvc.perform(get("/api/students")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_withInvalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/students")
                .header("Authorization", "Bearer this.is.not.a.valid.jwt"))
            .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
