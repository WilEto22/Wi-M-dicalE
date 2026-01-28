package com.example.crudApp.controller;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AdminController
 */
@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminController adminController;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .id(1L)
                .username("doctor@example.com")
                .fullName("Dr. Jean Dupont")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .roles("ROLE_USER")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .username("patient@example.com")
                .fullName("Marie Martin")
                .userType(UserType.PATIENT)
                .roles("ROLE_USER")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        ResponseEntity<List<User>> response = adminController.getAllUsers();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(testUser1, testUser2);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));

        // When
        ResponseEntity<User> response = adminController.getUserById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        assertThat(response.getBody().getUsername()).isEqualTo("doctor@example.com");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminController.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void updateUserRoles_ShouldUpdateRoles_WhenUserExists() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("roles", "ROLE_USER,ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        ResponseEntity<User> response = adminController.updateUserRoles(1L, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserRoles_ShouldThrowException_WhenUserNotFound() {
        // Given
        Map<String, String> request = new HashMap<>();
        request.put("roles", "ROLE_USER,ROLE_ADMIN");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminController.updateUserRoles(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        ResponseEntity<Map<String, String>> response = adminController.deleteUser(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo("Utilisateur supprimé avec succès");
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> adminController.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getStats_ShouldReturnStatistics() {
        // Given
        when(userRepository.count()).thenReturn(42L);

        // When
        ResponseEntity<Map<String, Object>> response = adminController.getStats();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalUsers")).isEqualTo(42L);
        assertThat(response.getBody().get("message")).isEqualTo("Statistiques du système");
        verify(userRepository, times(1)).count();
    }

    @Test
    void getStats_ShouldReturnZero_WhenNoUsers() {
        // Given
        when(userRepository.count()).thenReturn(0L);

        // When
        ResponseEntity<Map<String, Object>> response = adminController.getStats();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("totalUsers")).isEqualTo(0L);
        verify(userRepository, times(1)).count();
    }
}
