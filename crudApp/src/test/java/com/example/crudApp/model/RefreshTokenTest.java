package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenTest {

    @Test
    void testBuilder() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        // When
        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-123")
                .user(user)
                .expiryDate(expiryDate)
                .build();

        // Then
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getToken()).isEqualTo("refresh-token-123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isEqualTo(expiryDate);
    }

    @Test
    void testIsExpiredReturnsFalseForValidToken() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant futureDate = Instant.now().plusSeconds(3600);

        RefreshToken token = RefreshToken.builder()
                .token("refresh-token-123")
                .user(user)
                .expiryDate(futureDate)
                .build();

        // When
        boolean expired = token.isExpired();

        // Then
        assertThat(expired).isFalse();
    }

    @Test
    void testIsExpiredReturnsTrueForExpiredToken() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant pastDate = Instant.now().minusSeconds(3600);

        RefreshToken token = RefreshToken.builder()
                .token("refresh-token-123")
                .user(user)
                .expiryDate(pastDate)
                .build();

        // When
        boolean expired = token.isExpired();

        // Then
        assertThat(expired).isTrue();
    }

    @Test
    void testIsExpiredReturnsTrueForExactlyNow() throws InterruptedException {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant now = Instant.now();

        RefreshToken token = RefreshToken.builder()
                .token("refresh-token-123")
                .user(user)
                .expiryDate(now)
                .build();

        // Wait a tiny bit to ensure time has passed
        Thread.sleep(10);

        // When
        boolean expired = token.isExpired();

        // Then
        assertThat(expired).isTrue();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        RefreshToken token = new RefreshToken();

        // Then
        assertThat(token).isNotNull();
        assertThat(token.getId()).isNull();
        assertThat(token.getToken()).isNull();
        assertThat(token.getUser()).isNull();
        assertThat(token.getExpiryDate()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        // When
        RefreshToken token = new RefreshToken(
                1L,
                "refresh-token-123",
                user,
                expiryDate
        );

        // Then
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getToken()).isEqualTo("refresh-token-123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isEqualTo(expiryDate);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        RefreshToken token = new RefreshToken();
        User user = User.builder().id(1L).username("testuser").build();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        // When
        token.setId(1L);
        token.setToken("refresh-token-123");
        token.setUser(user);
        token.setExpiryDate(expiryDate);

        // Then
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getToken()).isEqualTo("refresh-token-123");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isEqualTo(expiryDate);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        RefreshToken token1 = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-123")
                .user(user)
                .expiryDate(expiryDate)
                .build();

        RefreshToken token2 = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-123")
                .user(user)
                .expiryDate(expiryDate)
                .build();

        // Then
        assertThat(token1).isEqualTo(token2);
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User user = User.builder().id(1L).username("testuser").build();
        Instant expiryDate = Instant.now().plusSeconds(3600);

        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .token("refresh-token-123")
                .user(user)
                .expiryDate(expiryDate)
                .build();

        // When
        String result = token.toString();

        // Then
        assertThat(result).contains("refresh-token-123");
    }
}
