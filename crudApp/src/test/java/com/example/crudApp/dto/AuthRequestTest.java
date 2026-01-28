package com.example.crudApp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidAuthRequest() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testUsernameIsRequired() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setPassword("password123");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Le nom d'utilisateur est obligatoire");
    }

    @Test
    void testUsernameMinLength() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("ab");
        request.setPassword("password123");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("entre 3 et 50 caractères");
    }

    @Test
    void testUsernameMaxLength() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("a".repeat(51));
        request.setPassword("password123");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("entre 3 et 50 caractères");
    }

    @Test
    void testPasswordIsRequired() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Le mot de passe est obligatoire");
    }

    @Test
    void testPasswordMinLength() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("12345");

        // When
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("au moins 6 caractères");
    }

    @Test
    void testGettersAndSetters() {
        // Given
        AuthRequest request = new AuthRequest();

        // When
        request.setUsername("testuser");
        request.setPassword("password123");

        // Then
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("password123");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        AuthRequest request1 = new AuthRequest();
        request1.setUsername("testuser");
        request1.setPassword("password123");

        AuthRequest request2 = new AuthRequest();
        request2.setUsername("testuser");
        request2.setPassword("password123");

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // When
        String result = request.toString();

        // Then
        assertThat(result).contains("testuser");
        assertThat(result).contains("password123");
    }
}
