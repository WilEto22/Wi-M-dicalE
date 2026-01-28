package com.example.crudApp.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenResponseTest {

    @Test
    void testBuilder() {
        // When
        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .tokenType("Bearer")
                .build();

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void testBuilderWithDefaultTokenType() {
        // When
        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RefreshTokenResponse response = new RefreshTokenResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshToken()).isNull();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void testAllArgsConstructor() {
        // When
        RefreshTokenResponse response = new RefreshTokenResponse(
                "access-token-123",
                "refresh-token-456",
                "Bearer",
                "testuser",
                com.example.crudApp.model.UserType.DOCTOR
        );

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token-123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-456");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getUserType()).isEqualTo(com.example.crudApp.model.UserType.DOCTOR);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        RefreshTokenResponse response = new RefreshTokenResponse();

        // When
        response.setAccessToken("new-access-token");
        response.setRefreshToken("new-refresh-token");
        response.setTokenType("Custom");

        // Then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Custom");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RefreshTokenResponse response1 = RefreshTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .tokenType("Bearer")
                .build();

        RefreshTokenResponse response2 = RefreshTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .tokenType("Bearer")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .build();
        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("access-token-123");
        assertThat(result).contains("refresh-token-456");
        assertThat(result).contains("Bearer");
    }
}
