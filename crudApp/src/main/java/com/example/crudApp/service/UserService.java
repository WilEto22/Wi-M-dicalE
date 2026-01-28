package com.example.crudApp.service;

import com.example.crudApp.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User saveNewUser(User user) {
        // Vérifier l'unicité de l'email
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("L'adresse email '" + user.getEmail() + "' est déjà utilisée");
        }

        // Vérifier l'unicité du numéro de téléphone
        if (user.getPhoneNumber() != null && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new DuplicateResourceException("Le numéro de téléphone '" + user.getPhoneNumber() + "' est déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) {
            user.setRoles("ROLE_USER");
        }
        return userRepository.save(user);
    }
}
