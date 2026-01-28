package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.exception.TokenRefreshException;
import com.example.crudApp.model.RefreshToken;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.RefreshTokenRepository;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        // Set the refresh token duration to 7 days (in milliseconds)
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("test-refresh-token")
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000L))
                .build();
    }

    @Test
    void testCreateRefreshToken_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // When
        RefreshToken result = refreshTokenService.createRefreshToken("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testCreateRefreshToken_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.createRefreshToken("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void testFindByToken_Success() {
        // Given
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(refreshToken));

        // When
        RefreshToken result = refreshTokenService.findByToken("test-refresh-token");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("test-refresh-token");
    }

    @Test
    void testFindByToken_NotFound() {
        // Given
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.findByToken("invalid-token"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Refresh token not found");
    }

    @Test
    void testVerifyExpiration_ValidToken() {
        // Given
        RefreshToken validToken = RefreshToken.builder()
                .id(1L)
                .token("valid-token")
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000L))
                .build();

        // When
        RefreshToken result = refreshTokenService.verifyExpiration(validToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isExpired()).isFalse();
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void testVerifyExpiration_ExpiredToken() {
        // Given
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .token("expired-token")
                .user(user)
                .expiryDate(Instant.now().minusMillis(1000L))
                .build();

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.verifyExpiration(expiredToken))
                .isInstanceOf(TokenRefreshException.class)
                .hasMessageContaining("Refresh token was expired");

        verify(refreshTokenRepository).delete(expiredToken);
    }

    @Test
    void testDeleteByToken() {
        // When
        refreshTokenService.deleteByToken("test-refresh-token");

        // Then
        verify(refreshTokenRepository).deleteByToken("test-refresh-token");
    }

    @Test
    void testDeleteByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        refreshTokenService.deleteByUsername("testuser");

        // Then
        verify(refreshTokenRepository).deleteByUser(user);
    }

    @Test
    void testDeleteByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> refreshTokenService.deleteByUsername("nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(refreshTokenRepository, never()).deleteByUser(any(User.class));
    }
}
