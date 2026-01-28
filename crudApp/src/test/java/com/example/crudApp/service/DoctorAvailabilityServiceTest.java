package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailability;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.repository.DoctorAvailabilityRepository;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DoctorAvailabilityService
 */
@ExtendWith(MockitoExtension.class)
class DoctorAvailabilityServiceTest {

    @Mock
    private DoctorAvailabilityRepository availabilityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DoctorAvailabilityService doctorAvailabilityService;

    private User testDoctor;
    private User testPatient;
    private DoctorAvailability testAvailability;

    @BeforeEach
    void setUp() {
        testDoctor = User.builder()
                .id(1L)
                .username("doctor@example.com")
                .fullName("Dr. Jean Dupont")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        testPatient = User.builder()
                .id(2L)
                .username("patient@example.com")
                .fullName("Marie Martin")
                .userType(UserType.PATIENT)
                .build();

        testAvailability = DoctorAvailability.builder()
                .id(1L)
                .doctor(testDoctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .isActive(true)
                .build();
    }

    @Test
    void createAvailability_ShouldCreateSuccessfully_WhenDoctorExists() {
        // Given
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(availabilityRepository.save(any(DoctorAvailability.class))).thenReturn(testAvailability);

        DoctorAvailability newAvailability = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .isActive(true)
                .build();

        // When
        DoctorAvailability result = doctorAvailabilityService.createAvailability(newAvailability, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDoctor()).isEqualTo(testDoctor);
        verify(userRepository, times(1)).findByUsername("doctor@example.com");
        verify(availabilityRepository, times(1)).save(any(DoctorAvailability.class));
    }

    @Test
    void createAvailability_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        DoctorAvailability newAvailability = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.createAvailability(newAvailability, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé");

        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
        verify(availabilityRepository, never()).save(any(DoctorAvailability.class));
    }

    @Test
    void createAvailability_ShouldThrowException_WhenUserIsNotDoctor() {
        // Given
        when(userRepository.findByUsername("patient@example.com")).thenReturn(Optional.of(testPatient));

        DoctorAvailability newAvailability = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.createAvailability(newAvailability, "patient@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Seuls les médecins peuvent définir des disponibilités");

        verify(userRepository, times(1)).findByUsername("patient@example.com");
        verify(availabilityRepository, never()).save(any(DoctorAvailability.class));
    }

    @Test
    void getDoctorAvailabilities_ShouldReturnAvailabilities_WhenDoctorExists() {
        // Given
        List<DoctorAvailability> availabilities = Arrays.asList(testAvailability);
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(availabilityRepository.findByDoctor(testDoctor)).thenReturn(availabilities);

        // When
        List<DoctorAvailability> result = doctorAvailabilityService.getDoctorAvailabilities("doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testAvailability);
        verify(userRepository, times(1)).findByUsername("doctor@example.com");
        verify(availabilityRepository, times(1)).findByDoctor(testDoctor);
    }

    @Test
    void getDoctorAvailabilities_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.getDoctorAvailabilities("nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé");

        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
        verify(availabilityRepository, never()).findByDoctor(any(User.class));
    }

    @Test
    void getActiveDoctorAvailabilities_ShouldReturnActiveAvailabilities_WhenDoctorExists() {
        // Given
        List<DoctorAvailability> activeAvailabilities = Arrays.asList(testAvailability);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(availabilityRepository.findByDoctorAndIsActiveTrue(testDoctor)).thenReturn(activeAvailabilities);

        // When
        List<DoctorAvailability> result = doctorAvailabilityService.getActiveDoctorAvailabilities(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(userRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).findByDoctorAndIsActiveTrue(testDoctor);
    }

    @Test
    void getActiveDoctorAvailabilities_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.getActiveDoctorAvailabilities(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé avec l'ID: 999");

        verify(userRepository, times(1)).findById(999L);
        verify(availabilityRepository, never()).findByDoctorAndIsActiveTrue(any(User.class));
    }

    @Test
    void updateAvailability_ShouldUpdateSuccessfully_WhenAuthorized() {
        // Given
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));
        when(availabilityRepository.save(any(DoctorAvailability.class))).thenReturn(testAvailability);

        DoctorAvailability updatedData = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .slotDurationMinutes(45)
                .isActive(false)
                .build();

        // When
        DoctorAvailability result = doctorAvailabilityService.updateAvailability(1L, updatedData, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).save(any(DoctorAvailability.class));
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenAvailabilityNotFound() {
        // Given
        when(availabilityRepository.findById(999L)).thenReturn(Optional.empty());

        DoctorAvailability updatedData = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .build();

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.updateAvailability(999L, updatedData, "doctor@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Disponibilité non trouvée avec l'ID: 999");

        verify(availabilityRepository, times(1)).findById(999L);
        verify(availabilityRepository, never()).save(any(DoctorAvailability.class));
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        DoctorAvailability updatedData = DoctorAvailability.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .build();

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.updateAvailability(1L, updatedData, "otherdoctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vous n'êtes pas autorisé à modifier cette disponibilité");

        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, never()).save(any(DoctorAvailability.class));
    }

    @Test
    void deleteAvailability_ShouldDeleteSuccessfully_WhenAuthorized() {
        // Given
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));
        doNothing().when(availabilityRepository).delete(testAvailability);

        // When
        doctorAvailabilityService.deleteAvailability(1L, "doctor@example.com");

        // Then
        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, times(1)).delete(testAvailability);
    }

    @Test
    void deleteAvailability_ShouldThrowException_WhenAvailabilityNotFound() {
        // Given
        when(availabilityRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.deleteAvailability(999L, "doctor@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Disponibilité non trouvée avec l'ID: 999");

        verify(availabilityRepository, times(1)).findById(999L);
        verify(availabilityRepository, never()).delete(any(DoctorAvailability.class));
    }

    @Test
    void deleteAvailability_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(availabilityRepository.findById(1L)).thenReturn(Optional.of(testAvailability));

        // When & Then
        assertThatThrownBy(() -> doctorAvailabilityService.deleteAvailability(1L, "otherdoctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vous n'êtes pas autorisé à supprimer cette disponibilité");

        verify(availabilityRepository, times(1)).findById(1L);
        verify(availabilityRepository, never()).delete(any(DoctorAvailability.class));
    }
}
