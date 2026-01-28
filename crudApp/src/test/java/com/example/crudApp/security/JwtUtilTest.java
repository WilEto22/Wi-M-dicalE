package com.example.crudApp.security;

import com.example.crudApp.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private String testUsername;
    private String token;

    @BeforeEach
    void setUp() {
        testUsername = "testuser";
        token = jwtUtil.generateToken(testUsername);
    }

    @Test
    void generateToken_ShouldReturnNonEmptyToken() {
        // When
        String generatedToken = jwtUtil.generateToken("user123");

        // Then
        assertThat(generatedToken).isNotNull();
        assertThat(generatedToken).isNotEmpty();
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValidAndUsernameMatches() {
        // When
        String extractedUsername = jwtUtil.extractUsername(token);
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
        assertThat(extractedUsername).isEqualTo(testUsername);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void generateToken_ShouldGenerateDifferentTokensForDifferentUsers() {
        // When
        String token1 = jwtUtil.generateToken("user1");
        String token2 = jwtUtil.generateToken("user2");

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void extractUsername_ShouldWorkForMultipleTokens() {
        // Given
        String user1 = "alice";
        String user2 = "bob";
        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        // When
        String extracted1 = jwtUtil.extractUsername(token1);
        String extracted2 = jwtUtil.extractUsername(token2);

        // Then
        assertThat(extracted1).isEqualTo(user1);
        assertThat(extracted2).isEqualTo(user2);
    }
}
