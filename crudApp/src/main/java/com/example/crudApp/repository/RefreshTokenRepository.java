package com.example.crudApp.repository;

import com.example.crudApp.model.RefreshToken;
import com.example.crudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Transactional
    int deleteByUser(User user);

    @Modifying
    @Transactional
    int deleteByToken(String token);
}
