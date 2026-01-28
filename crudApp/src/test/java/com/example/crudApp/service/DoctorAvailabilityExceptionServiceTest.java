package com.example.crudApp.service;

import com.example.crudApp.dto.AvailabilityExceptionRequest;
import com.example.crudApp.dto.AvailabilityExceptionResponse;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailabilityException;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.repository.DoctorAvailabilityExceptionRepository;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DoctorAvailabilityExceptionService
 */
@ExtendWith(MockitoExtension.class)
class DoctorAvailabilityExceptionServiceTest {

    @Mock
    private DoctorAvailabilityExceptionRepository exceptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DoctorAvailabilityExceptionService exceptionService;

    private User testDoctor;
    private DoctorAvailabilityException testException;
    private AvailabilityExceptionRequest testRequest;

    @BeforeEach
    void setUp() {
        testDoctor = User.builder()
                .id(1L)
                .username("doctor@example.com")
                .fullName("Dr. Jean Dupont")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        testException = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(testDoctor)
                .exceptionDate(LocalDate.of(2024, 12, 25))
                .reason("Congés de Noël")
                .isAvailable(false)
                .isActive(true)
                .createdAt(LocalDate.now())
                .build();

        testRequest = new AvailabilityExceptionRequest();
        testRequest.setExceptionDate(LocalDate.of(2024, 12, 25));
        testRequest.setReason("Congés de Noël");
        testRequest.setIsAvailable(false);
    }

    @Test
    void createException_ShouldCreateSuccessfully_WhenDoctorExists() {
        // Given
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(exceptionRepository.findByDoctorAndExceptionDateAndIsActive(testDoctor, testRequest.getExceptionDate(), true))
                .thenReturn(Optional.empty());
        when(exceptionRepository.save(any(DoctorAvailabilityException.class))).thenReturn(testException);

        // When
        AvailabilityExceptionResponse result = exceptionService.createException(testRequest, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).findByUsername("doctor@example.com");
        verify(exceptionRepository, times(1)).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void createException_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exceptionService.createException(testRequest, "nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé");

        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void createException_ShouldThrowException_WhenExceptionAlreadyExists() {
        // Given
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(exceptionRepository.findByDoctorAndExceptionDateAndIsActive(testDoctor, testRequest.getExceptionDate(), true))
                .thenReturn(Optional.of(testException));

        // When & Then
        assertThatThrownBy(() -> exceptionService.createException(testRequest, "doctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Une exception existe déjà pour la date");

        verify(userRepository, times(1)).findByUsername("doctor@example.com");
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void createException_ShouldSetDefaultIsAvailable_WhenNull() {
        // Given
        AvailabilityExceptionRequest requestWithNullAvailable = new AvailabilityExceptionRequest();
        requestWithNullAvailable.setExceptionDate(LocalDate.of(2024, 12, 25));
        requestWithNullAvailable.setReason("Test");
        requestWithNullAvailable.setIsAvailable(null);

        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(exceptionRepository.findByDoctorAndExceptionDateAndIsActive(any(), any(), anyBoolean()))
                .thenReturn(Optional.empty());
        when(exceptionRepository.save(any(DoctorAvailabilityException.class))).thenReturn(testException);

        // When
        AvailabilityExceptionResponse result = exceptionService.createException(requestWithNullAvailable, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(exceptionRepository, times(1)).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void getDoctorExceptions_ShouldReturnExceptions_WhenDoctorExists() {
        // Given
        List<DoctorAvailabilityException> exceptions = Arrays.asList(testException);
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));
        when(exceptionRepository.findByDoctorAndIsActiveOrderByExceptionDateAsc(testDoctor, true))
                .thenReturn(exceptions);

        // When
        List<AvailabilityExceptionResponse> result = exceptionService.getDoctorExceptions("doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findByUsername("doctor@example.com");
        verify(exceptionRepository, times(1)).findByDoctorAndIsActiveOrderByExceptionDateAsc(testDoctor, true);
    }

    @Test
    void getDoctorExceptions_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exceptionService.getDoctorExceptions("nonexistent@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé");

        verify(userRepository, times(1)).findByUsername("nonexistent@example.com");
    }

    @Test
    void getExceptionById_ShouldReturnException_WhenAuthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));

        // When
        AvailabilityExceptionResponse result = exceptionService.getExceptionById(1L, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(exceptionRepository, times(1)).findById(1L);
    }

    @Test
    void getExceptionById_ShouldThrowException_WhenNotFound() {
        // Given
        when(exceptionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exceptionService.getExceptionById(999L, "doctor@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exception non trouvée avec l'ID: 999");

        verify(exceptionRepository, times(1)).findById(999L);
    }

    @Test
    void getExceptionById_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));

        // When & Then
        assertThatThrownBy(() -> exceptionService.getExceptionById(1L, "otherdoctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vous n'avez pas accès à cette exception");

        verify(exceptionRepository, times(1)).findById(1L);
    }

    @Test
    void updateException_ShouldUpdateSuccessfully_WhenAuthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));
        when(exceptionRepository.save(any(DoctorAvailabilityException.class))).thenReturn(testException);

        // When
        AvailabilityExceptionResponse result = exceptionService.updateException(1L, testRequest, "doctor@example.com");

        // Then
        assertThat(result).isNotNull();
        verify(exceptionRepository, times(1)).findById(1L);
        verify(exceptionRepository, times(1)).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void updateException_ShouldThrowException_WhenNotFound() {
        // Given
        when(exceptionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exceptionService.updateException(999L, testRequest, "doctor@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exception non trouvée avec l'ID: 999");

        verify(exceptionRepository, times(1)).findById(999L);
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void updateException_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));

        // When & Then
        assertThatThrownBy(() -> exceptionService.updateException(1L, testRequest, "otherdoctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vous n'avez pas accès à cette exception");

        verify(exceptionRepository, times(1)).findById(1L);
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void deleteException_ShouldDeleteSuccessfully_WhenAuthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));
        when(exceptionRepository.save(any(DoctorAvailabilityException.class))).thenReturn(testException);

        // When
        exceptionService.deleteException(1L, "doctor@example.com");

        // Then
        verify(exceptionRepository, times(1)).findById(1L);
        verify(exceptionRepository, times(1)).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void deleteException_ShouldThrowException_WhenNotFound() {
        // Given
        when(exceptionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exceptionService.deleteException(999L, "doctor@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Exception non trouvée avec l'ID: 999");

        verify(exceptionRepository, times(1)).findById(999L);
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void deleteException_ShouldThrowException_WhenUnauthorized() {
        // Given
        when(exceptionRepository.findById(1L)).thenReturn(Optional.of(testException));

        // When & Then
        assertThatThrownBy(() -> exceptionService.deleteException(1L, "otherdoctor@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Vous n'avez pas accès à cette exception");

        verify(exceptionRepository, times(1)).findById(1L);
        verify(exceptionRepository, never()).save(any(DoctorAvailabilityException.class));
    }

    @Test
    void getExceptionForDate_ShouldReturnException_WhenExists() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        when(exceptionRepository.findByDoctorAndExceptionDateAndIsActive(testDoctor, testDate, true))
                .thenReturn(Optional.of(testException));

        // When
        DoctorAvailabilityException result = exceptionService.getExceptionForDate(testDoctor, testDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(testException);
        verify(exceptionRepository, times(1)).findByDoctorAndExceptionDateAndIsActive(testDoctor, testDate, true);
    }

    @Test
    void getExceptionForDate_ShouldReturnNull_WhenNotExists() {
        // Given
        LocalDate testDate = LocalDate.of(2024, 12, 25);
        when(exceptionRepository.findByDoctorAndExceptionDateAndIsActive(testDoctor, testDate, true))
                .thenReturn(Optional.empty());

        // When
        DoctorAvailabilityException result = exceptionService.getExceptionForDate(testDoctor, testDate);

        // Then
        assertThat(result).isNull();
        verify(exceptionRepository, times(1)).findByDoctorAndExceptionDateAndIsActive(testDoctor, testDate, true);
    }

    @Test
    void getExceptionsForDateRange_ShouldReturnExceptions() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        List<DoctorAvailabilityException> exceptions = Arrays.asList(testException);
        when(exceptionRepository.findByDoctorAndExceptionDateBetweenAndIsActive(testDoctor, startDate, endDate, true))
                .thenReturn(exceptions);

        // When
        List<DoctorAvailabilityException> result = exceptionService.getExceptionsForDateRange(testDoctor, startDate, endDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testException);
        verify(exceptionRepository, times(1)).findByDoctorAndExceptionDateBetweenAndIsActive(testDoctor, startDate, endDate, true);
    }
}
