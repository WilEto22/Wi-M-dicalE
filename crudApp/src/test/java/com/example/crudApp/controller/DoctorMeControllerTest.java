package com.example.crudApp.controller;

import com.example.crudApp.dto.AvailabilityExceptionRequest;
import com.example.crudApp.dto.AvailabilityExceptionResponse;
import com.example.crudApp.dto.DoctorResponse;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.*;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.service.DoctorAvailabilityExceptionService;
import com.example.crudApp.service.DoctorAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour DoctorMeController
 */
@ExtendWith(MockitoExtension.class)
class DoctorMeControllerTest {

    @Mock
    private DoctorAvailabilityService availabilityService;

    @Mock
    private DoctorAvailabilityExceptionService exceptionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DoctorMeController doctorMeController;

    private User testDoctor;
    private User testPatient;
    private DoctorAvailability testAvailability;
    private AvailabilityExceptionRequest testExceptionRequest;
    private AvailabilityExceptionResponse testExceptionResponse;

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

        testExceptionRequest = new AvailabilityExceptionRequest();
        testExceptionRequest.setExceptionDate(LocalDate.of(2024, 12, 25));
        testExceptionRequest.setReason("Congés de Noël");
        testExceptionRequest.setIsAvailable(false);

        testExceptionResponse = new AvailabilityExceptionResponse();
        testExceptionResponse.setId(1L);
        testExceptionResponse.setExceptionDate(LocalDate.of(2024, 12, 25));
        testExceptionResponse.setReason("Congés de Noël");
        testExceptionResponse.setIsAvailable(false);

        when(authentication.getName()).thenReturn("doctor@example.com");
    }

    @Test
    void getMyProfile_ShouldReturnDoctorProfile_WhenDoctorExists() {
        // Given
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.of(testDoctor));

        // When
        ResponseEntity<DoctorResponse> response = doctorMeController.getMyProfile(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(userRepository, times(1)).findByUsername("doctor@example.com");
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenDoctorNotFound() {
        // Given
        when(userRepository.findByUsername("doctor@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> doctorMeController.getMyProfile(authentication))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Médecin non trouvé");

        verify(userRepository, times(1)).findByUsername("doctor@example.com");
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenUserIsNotDoctor() {
        // Given
        when(userRepository.findByUsername("patient@example.com")).thenReturn(Optional.of(testPatient));
        when(authentication.getName()).thenReturn("patient@example.com");

        // When & Then
        assertThatThrownBy(() -> doctorMeController.getMyProfile(authentication))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("L'utilisateur connecté n'est pas un médecin");

        verify(userRepository, times(1)).findByUsername("patient@example.com");
    }

    @Test
    void createMyAvailability_ShouldCreateSuccessfully() {
        // Given
        when(availabilityService.createAvailability(any(DoctorAvailability.class), anyString()))
                .thenReturn(testAvailability);

        // When
        ResponseEntity<DoctorAvailability> response = doctorMeController.createMyAvailability(testAvailability, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(availabilityService, times(1)).createAvailability(any(DoctorAvailability.class), eq("doctor@example.com"));
    }

    @Test
    void getMyAvailabilities_ShouldReturnAvailabilities() {
        // Given
        List<DoctorAvailability> availabilities = Arrays.asList(testAvailability);
        when(availabilityService.getDoctorAvailabilities("doctor@example.com")).thenReturn(availabilities);

        // When
        ResponseEntity<List<DoctorAvailability>> response = doctorMeController.getMyAvailabilities(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(availabilityService, times(1)).getDoctorAvailabilities("doctor@example.com");
    }

    @Test
    void updateMyAvailability_ShouldUpdateSuccessfully() {
        // Given
        DoctorAvailability updatedAvailability = DoctorAvailability.builder()
                .id(1L)
                .doctor(testDoctor)
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .slotDurationMinutes(45)
                .isActive(true)
                .build();

        when(availabilityService.updateAvailability(eq(1L), any(DoctorAvailability.class), eq("doctor@example.com")))
                .thenReturn(updatedAvailability);

        // When
        ResponseEntity<DoctorAvailability> response = doctorMeController.updateMyAvailability(1L, updatedAvailability, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(availabilityService, times(1)).updateAvailability(eq(1L), any(DoctorAvailability.class), eq("doctor@example.com"));
    }

    @Test
    void deleteMyAvailability_ShouldDeleteSuccessfully() {
        // Given
        doNothing().when(availabilityService).deleteAvailability(1L, "doctor@example.com");

        // When
        ResponseEntity<Void> response = doctorMeController.deleteMyAvailability(1L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(availabilityService, times(1)).deleteAvailability(1L, "doctor@example.com");
    }

    @Test
    void createException_ShouldCreateSuccessfully() {
        // Given
        when(exceptionService.createException(any(AvailabilityExceptionRequest.class), eq("doctor@example.com")))
                .thenReturn(testExceptionResponse);

        // When
        ResponseEntity<AvailabilityExceptionResponse> response = doctorMeController.createException(testExceptionRequest, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(exceptionService, times(1)).createException(any(AvailabilityExceptionRequest.class), eq("doctor@example.com"));
    }

    @Test
    void getMyExceptions_ShouldReturnExceptions() {
        // Given
        List<AvailabilityExceptionResponse> exceptions = Arrays.asList(testExceptionResponse);
        when(exceptionService.getDoctorExceptions("doctor@example.com")).thenReturn(exceptions);

        // When
        ResponseEntity<List<AvailabilityExceptionResponse>> response = doctorMeController.getMyExceptions(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(exceptionService, times(1)).getDoctorExceptions("doctor@example.com");
    }

    @Test
    void getExceptionById_ShouldReturnException() {
        // Given
        when(exceptionService.getExceptionById(1L, "doctor@example.com")).thenReturn(testExceptionResponse);

        // When
        ResponseEntity<AvailabilityExceptionResponse> response = doctorMeController.getExceptionById(1L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
        verify(exceptionService, times(1)).getExceptionById(1L, "doctor@example.com");
    }

    @Test
    void updateException_ShouldUpdateSuccessfully() {
        // Given
        when(exceptionService.updateException(eq(1L), any(AvailabilityExceptionRequest.class), eq("doctor@example.com")))
                .thenReturn(testExceptionResponse);

        // When
        ResponseEntity<AvailabilityExceptionResponse> response = doctorMeController.updateException(1L, testExceptionRequest, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(exceptionService, times(1)).updateException(eq(1L), any(AvailabilityExceptionRequest.class), eq("doctor@example.com"));
    }

    @Test
    void deleteException_ShouldDeleteSuccessfully() {
        // Given
        doNothing().when(exceptionService).deleteException(1L, "doctor@example.com");

        // When
        ResponseEntity<Void> response = doctorMeController.deleteException(1L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(exceptionService, times(1)).deleteException(1L, "doctor@example.com");
    }
}
