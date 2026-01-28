package com.example.crudApp.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthResponseTest {

    @Test
    void testAllArgsConstructor() {
        // When
        AuthResponse response = new AuthResponse("token123", "testuser", "Login successful");

        // Then
        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getMessage()).isEqualTo("Login successful");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        AuthResponse response = new AuthResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testTokenOnlyConstructor() {
        // When
        AuthResponse response = new AuthResponse("token123");

        // Then
        assertThat(response.getToken()).isEqualTo("token123");
        assertThat(response.getUsername()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AuthResponse response = new AuthResponse();

        // When
        response.setToken("newToken");
        response.setUsername("newUser");
        response.setMessage("New message");

        // Then
        assertThat(response.getToken()).isEqualTo("newToken");
        assertThat(response.getUsername()).isEqualTo("newUser");
        assertThat(response.getMessage()).isEqualTo("New message");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        AuthResponse response1 = new AuthResponse("token123", "testuser", "message");
        AuthResponse response2 = new AuthResponse("token123", "testuser", "message");

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AuthResponse response = new AuthResponse("token123", "testuser", "message");

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("token123");
        assertThat(result).contains("testuser");
        assertThat(result).contains("message");
    }
}
