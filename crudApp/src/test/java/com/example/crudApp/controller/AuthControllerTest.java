package com.example.crudApp.controller;

import com.example.crudApp.config.TestSecurityConfig;
import com.example.crudApp.dto.AuthRequest;
import com.example.crudApp.dto.RegisterRequest;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.security.JwtUtil;
import com.example.crudApp.service.EmailService;
import com.example.crudApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // No need to delete all - @Transactional will rollback after each test
    }

    @Test
    void register_ShouldReturnToken_WhenValidRequest() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("newuser@example.com");
        request.setPassword("password123");
        request.setUserType(com.example.crudApp.model.UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        String jsonContent = objectMapper.writeValueAsString(request);
        System.out.println("JSON envoyé: " + jsonContent);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(result -> System.out.println("Réponse: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void register_ShouldReturn400_WhenUsernameTooShort() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setPassword("password123");
        request.setUserType(com.example.crudApp.model.UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erreur de validation"))
                .andExpect(jsonPath("$.errors.username").value("Le nom d'utilisateur doit contenir entre 3 et 50 caractères"));
    }

    @Test
    void register_ShouldReturn400_WhenPasswordTooShort() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("validuser");
        request.setPassword("123");
        request.setUserType(com.example.crudApp.model.UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erreur de validation"))
                .andExpect(jsonPath("$.errors.password").value("Le mot de passe doit contenir au moins 6 caractères"));
    }

    @Test
    void register_ShouldReturn400_WhenFieldsAreBlank() throws Exception {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("");
        // userType and specialty are null - should trigger validation errors

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.errors.userType").exists());
    }

    @Test
    void register_ShouldReturn409_WhenUsernameAlreadyExists() throws Exception {
        // Given - Create existing user
        User existingUser = User.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("password123")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.PEDIATRIE)
                .build();
        userService.saveNewUser(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("newemail@example.com");
        request.setPassword("password123");
        request.setUserType(com.example.crudApp.model.UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Le nom d'utilisateur 'existinguser' est déjà utilisé"));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        // Given - Create user
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.NEUROLOGIE)
                .build();
        userService.saveNewUser(user);

        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void login_ShouldReturn401_WhenPasswordIsIncorrect() throws Exception {
        // Given - Create user
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.DERMATOLOGIE)
                .build();
        userService.saveNewUser(user);

        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Nom d'utilisateur ou mot de passe incorrect"));
    }

    @Test
    void login_ShouldReturn401_WhenUserDoesNotExist() throws Exception {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturn400_WhenValidationFails() throws Exception {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("ab");
        request.setPassword("123");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }
}
