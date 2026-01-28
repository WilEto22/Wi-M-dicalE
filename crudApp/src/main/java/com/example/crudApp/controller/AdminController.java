package com.example.crudApp.controller;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les opérations d'administration (réservé aux ADMIN)
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Admin: Récupération de tous les utilisateurs");
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        logger.info("Admin: Récupération de l'utilisateur avec l'ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<User> updateUserRoles(@PathVariable Long id, @RequestBody Map<String, String> request) {
        logger.info("Admin: Mise à jour des rôles pour l'utilisateur ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String roles = request.get("roles");
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        logger.info("Admin: Rôles mis à jour pour l'utilisateur: {}", user.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        logger.info("Admin: Suppression de l'utilisateur ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Utilisateur supprimé avec succès");
        logger.info("Admin: Utilisateur supprimé avec succès: {}", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        logger.info("Admin: Récupération des statistiques");

        long totalUsers = userRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("message", "Statistiques du système");

        return ResponseEntity.ok(stats);
    }
}
