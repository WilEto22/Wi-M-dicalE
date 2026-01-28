package com.example.crudApp.controller;

import com.example.crudApp.dto.AppointmentRequest;
import com.example.crudApp.dto.AppointmentResponse;
import com.example.crudApp.model.*;
import com.example.crudApp.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AppointmentController
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AppointmentController appointmentController;

    private Appointment testAppointment;
    private AppointmentRequest testRequest;
    private User testDoctor;
    private User testPatient;

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

        testAppointment = Appointment.builder()
                .id(1L)
                .doctor(testDoctor)
                .patient(testPatient)
                .appointmentDateTime(LocalDateTime.now().plusDays(1))
                .reason("Consultation de routine")
                .status(AppointmentStatus.PENDING)
                .build();

        testRequest = new AppointmentRequest();
        testRequest.setDoctorId(1L);
        testRequest.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        testRequest.setReason("Consultation de routine");

        when(authentication.getName()).thenReturn("patient@example.com");
    }

    @Test
    void createAppointment_ShouldCreateSuccessfully() {
        // Given
        when(appointmentService.createAppointment(any(AppointmentRequest.class), anyString()))
                .thenReturn(testAppointment);

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.createAppointment(testRequest, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).createAppointment(any(AppointmentRequest.class), anyString());
    }

    @Test
    void getMyAppointments_ShouldReturnPatientAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getPatientAppointments("patient@example.com")).thenReturn(appointments);
        when(appointmentService.getDoctorAppointments("patient@example.com"))
                .thenThrow(new RuntimeException("Not a doctor"));

        // When
        ResponseEntity<List<AppointmentResponse>> response = appointmentController.getMyAppointments(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(appointmentService, times(1)).getPatientAppointments("patient@example.com");
    }

    @Test
    void getMyAppointments_ShouldReturnBothPatientAndDoctorAppointments() {
        // Given
        Appointment doctorAppointment = Appointment.builder()
                .id(2L)
                .doctor(testDoctor)
                .patient(testPatient)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .reason("Suivi")
                .status(AppointmentStatus.CONFIRMED)
                .build();

        List<Appointment> patientAppointments = new java.util.ArrayList<>(Arrays.asList(testAppointment));
        List<Appointment> doctorAppointments = Arrays.asList(doctorAppointment);

        when(authentication.getName()).thenReturn("doctor@example.com");
        when(appointmentService.getPatientAppointments("doctor@example.com")).thenReturn(patientAppointments);
        when(appointmentService.getDoctorAppointments("doctor@example.com")).thenReturn(doctorAppointments);

        // When
        ResponseEntity<List<AppointmentResponse>> response = appointmentController.getMyAppointments(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        verify(appointmentService, times(1)).getPatientAppointments("doctor@example.com");
        verify(appointmentService, times(1)).getDoctorAppointments("doctor@example.com");
    }

    @Test
    void getUpcomingAppointments_ShouldReturnUpcomingAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentService.getUpcomingPatientAppointments("patient@example.com")).thenReturn(appointments);
        when(appointmentService.getUpcomingDoctorAppointments("patient@example.com"))
                .thenThrow(new RuntimeException("Not a doctor"));

        // When
        ResponseEntity<List<AppointmentResponse>> response = appointmentController.getUpcomingAppointments(authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(appointmentService, times(1)).getUpcomingPatientAppointments("patient@example.com");
    }

    @Test
    void getAppointmentById_ShouldReturnAppointment() {
        // Given
        when(appointmentService.getAppointmentById(1L)).thenReturn(testAppointment);

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.getAppointmentById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).getAppointmentById(1L);
    }

    @Test
    void confirmAppointment_ShouldConfirmSuccessfully() {
        // Given
        testAppointment.setStatus(AppointmentStatus.CONFIRMED);
        when(appointmentService.confirmAppointment(1L, "doctor@example.com")).thenReturn(testAppointment);
        when(authentication.getName()).thenReturn("doctor@example.com");

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.confirmAppointment(1L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).confirmAppointment(1L, "doctor@example.com");
    }

    @Test
    void cancelAppointment_ShouldCancelSuccessfully() {
        // Given
        testAppointment.setStatus(AppointmentStatus.CANCELLED);
        when(appointmentService.cancelAppointment(1L, "patient@example.com")).thenReturn(testAppointment);

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.cancelAppointment(1L, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).cancelAppointment(1L, "patient@example.com");
    }

    @Test
    void completeAppointment_ShouldCompleteSuccessfully() {
        // Given
        testAppointment.setStatus(AppointmentStatus.COMPLETED);
        testAppointment.setDoctorNotes("Consultation terminée");
        when(appointmentService.completeAppointment(1L, "doctor@example.com", "Consultation terminée"))
                .thenReturn(testAppointment);
        when(authentication.getName()).thenReturn("doctor@example.com");

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.completeAppointment(
                1L, "Consultation terminée", authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).completeAppointment(1L, "doctor@example.com", "Consultation terminée");
    }

    @Test
    void completeAppointment_ShouldCompleteWithoutNotes() {
        // Given
        testAppointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentService.completeAppointment(1L, "doctor@example.com", null))
                .thenReturn(testAppointment);
        when(authentication.getName()).thenReturn("doctor@example.com");

        // When
        ResponseEntity<AppointmentResponse> response = appointmentController.completeAppointment(
                1L, null, authentication);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(appointmentService, times(1)).completeAppointment(1L, "doctor@example.com", null);
    }
}
