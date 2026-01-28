package com.example.crudApp.service;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.MEDECINE_GENERALE)
                .build();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void saveNewUser_ShouldEncodePasswordAndSaveUser() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encodedPassword";

        User userToSave = User.builder()
                .username("newuser")
                .password(rawPassword)
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.CHIRURGIE_GENERALE)
                .build();

        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .password(encodedPassword)
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.CHIRURGIE_GENERALE)
                .build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.saveNewUser(userToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getRoles()).isEqualTo("ROLE_USER");

        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveNewUser_ShouldSetDefaultRole_WhenRolesIsNull() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encodedPassword";

        User userToSave = User.builder()
                .username("newuser")
                .password(rawPassword)
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.OPHTALMOLOGIE)
                .build();

        User savedUser = User.builder()
                .id(1L)
                .username("newuser")
                .password(encodedPassword)
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.OPHTALMOLOGIE)
                .build();

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.saveNewUser(userToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoles()).isEqualTo("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveNewUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        User userToSave = User.builder()
                .username("newuser")
                .email("existing@example.com")
                .password("password123")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                com.example.crudApp.exception.DuplicateResourceException.class,
                () -> userService.saveNewUser(userToSave)
        )).hasMessageContaining("L'adresse email 'existing@example.com' est déjà utilisée");

        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveNewUser_ShouldThrowException_WhenPhoneNumberAlreadyExists() {
        // Given
        User userToSave = User.builder()
                .username("newuser")
                .phoneNumber("0612345678")
                .password("password123")
                .userType(com.example.crudApp.model.UserType.PATIENT)
                .build();

        when(userRepository.existsByPhoneNumber("0612345678")).thenReturn(true);

        // When & Then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                com.example.crudApp.exception.DuplicateResourceException.class,
                () -> userService.saveNewUser(userToSave)
        )).hasMessageContaining("Le numéro de téléphone '0612345678' est déjà utilisé");

        verify(userRepository, times(1)).existsByPhoneNumber("0612345678");
        verify(userRepository, never()).save(any(User.class));
    }
}
