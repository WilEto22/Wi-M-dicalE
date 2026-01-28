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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("$2a$10$encodedPassword")
                .roles("ROLE_USER,ROLE_ADMIN")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPassword");
        assertThat(userDetails.getAuthorities()).hasSize(2);
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: nonexistent");

        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void loadUserByUsername_ShouldHandleSingleRole() {
        // Given
        User singleRoleUser = User.builder()
                .id(2L)
                .username("simpleuser")
                .password("$2a$10$encodedPassword")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(MedicalSpecialty.PEDIATRIE)
                .build();

        when(userRepository.findByUsername("simpleuser")).thenReturn(Optional.of(singleRoleUser));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("simpleuser");

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }
}
