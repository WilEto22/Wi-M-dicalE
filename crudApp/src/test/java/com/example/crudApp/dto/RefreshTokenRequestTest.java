package com.example.crudApp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRefreshTokenRequest() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testRefreshTokenIsRequired() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Refresh token is required");
    }

    @Test
    void testRefreshTokenCannotBeBlank() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("   ");

        // When
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Refresh token is required");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RefreshTokenRequest request = new RefreshTokenRequest();

        // Then
        assertThat(request).isNotNull();
        assertThat(request.getRefreshToken()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        RefreshTokenRequest request = new RefreshTokenRequest("token123");

        // Then
        assertThat(request.getRefreshToken()).isEqualTo("token123");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();

        // When
        request.setRefreshToken("new-token");

        // Then
        assertThat(request.getRefreshToken()).isEqualTo("new-token");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RefreshTokenRequest request1 = new RefreshTokenRequest("token123");
        RefreshTokenRequest request2 = new RefreshTokenRequest("token123");

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("token123");

        // When
        String result = request.toString();

        // Then
        assertThat(result).contains("token123");
    }
}
