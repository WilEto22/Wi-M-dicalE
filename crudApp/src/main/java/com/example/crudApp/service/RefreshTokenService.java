package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.exception.TokenRefreshException;
import com.example.crudApp.model.RefreshToken;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.RefreshTokenRepository;
import com.example.crudApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service pour gérer les refresh tokens
 */
@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms:604800000}") // 7 jours par défautddoc
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crée un nouveau refresh token pour un utilisateur
     */
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return createRefreshTokenForUser(user);
    }

    /**
     * Crée un nouveau refresh token pour un utilisateur (objet User)
     */
    @Transactional
    public RefreshToken createRefreshTokenForUser(User user) {
        // Supprimer les anciens refresh tokens de cet utilisateur
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Trouve un refresh token par sa valeur
     */
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }

    /**
     * Vérifie si un refresh token est expiré et le supprime si c'est le cas
     */
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    /**
     * Supprime un refresh token
     */
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    /**
     * Supprime tous les refresh tokens d'un utilisateur
     */
    @Transactional
    public void deleteByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        refreshTokenRepository.deleteByUser(user);
    }
}
