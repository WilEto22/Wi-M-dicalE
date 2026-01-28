package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.AppointmentStatus;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentReminderServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentReminderService appointmentReminderService;

    private Appointment appointment;
    private User doctor;
    private User patient;

    @BeforeEach
    void setUp() {
        doctor = User.builder()
                .id(1L)
                .username("doctor1")
                .email("doctor@example.com")
                .build();

        patient = User.builder()
                .id(2L)
                .username("patient1")
                .email("patient@example.com")
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now().plusHours(24))
                .status(AppointmentStatus.CONFIRMED)
                .reason("Consultation")
                .build();
    }

    @Test
    void testSendAppointmentReminders_WithUpcomingAppointments() {
        // Given
        List<Appointment> upcomingAppointments = Arrays.asList(appointment);
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBetween(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(upcomingAppointments);

        doNothing().when(emailService).sendAppointmentReminderEmail(any(Appointment.class));

        // When
        appointmentReminderService.sendAppointmentReminders();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBetween(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(emailService).sendAppointmentReminderEmail(appointment);
    }

    @Test
    void testSendAppointmentReminders_NoUpcomingAppointments() {
        // Given
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBetween(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // When
        appointmentReminderService.sendAppointmentReminders();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBetween(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
        verify(emailService, never()).sendAppointmentReminderEmail(any(Appointment.class));
    }

    @Test
    void testSendAppointmentReminders_EmailServiceThrowsException() {
        // Given
        List<Appointment> upcomingAppointments = Arrays.asList(appointment);
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBetween(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(upcomingAppointments);

        doThrow(new RuntimeException("Email service error"))
                .when(emailService).sendAppointmentReminderEmail(any(Appointment.class));

        // When
        appointmentReminderService.sendAppointmentReminders();

        // Then - Should not throw exception, just log error
        verify(emailService).sendAppointmentReminderEmail(appointment);
    }

    @Test
    void testCleanupOldAppointments_WithOldCancelledAppointments() {
        // Given
        Appointment oldAppointment = Appointment.builder()
                .id(2L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now().minusMonths(7))
                .status(AppointmentStatus.CANCELLED)
                .build();

        List<Appointment> oldAppointments = Arrays.asList(oldAppointment);
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CANCELLED),
                any(LocalDateTime.class)
        )).thenReturn(oldAppointments);

        // When
        appointmentReminderService.cleanupOldAppointments();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CANCELLED),
                any(LocalDateTime.class)
        );
        verify(appointmentRepository).deleteAll(oldAppointments);
    }

    @Test
    void testCleanupOldAppointments_NoOldAppointments() {
        // Given
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CANCELLED),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // When
        appointmentReminderService.cleanupOldAppointments();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CANCELLED),
                any(LocalDateTime.class)
        );
        verify(appointmentRepository, never()).deleteAll(anyList());
    }

    @Test
    void testAutoCompletePassedAppointments_WithPassedAppointments() {
        // Given
        Appointment passedAppointment = Appointment.builder()
                .id(3L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now().minusHours(2))
                .status(AppointmentStatus.CONFIRMED)
                .build();

        List<Appointment> passedAppointments = Arrays.asList(passedAppointment);
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class)
        )).thenReturn(passedAppointments);

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(passedAppointment);

        // When
        appointmentReminderService.autoCompletePassedAppointments();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class)
        );
        verify(appointmentRepository).save(passedAppointment);
    }

    @Test
    void testAutoCompletePassedAppointments_NoPassedAppointments() {
        // Given
        when(appointmentRepository.findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        // When
        appointmentReminderService.autoCompletePassedAppointments();

        // Then
        verify(appointmentRepository).findByStatusAndAppointmentDateTimeBefore(
                eq(AppointmentStatus.CONFIRMED),
                any(LocalDateTime.class)
        );
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testSendImmediateReminder_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        doNothing().when(emailService).sendAppointmentReminderEmail(any(Appointment.class));

        // When
        appointmentReminderService.sendImmediateReminder(1L);

        // Then
        verify(appointmentRepository).findById(1L);
        verify(emailService).sendAppointmentReminderEmail(appointment);
    }

    @Test
    void testSendImmediateReminder_AppointmentNotFound() {
        // Given
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> appointmentReminderService.sendImmediateReminder(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rendez-vous non trouvé");

        verify(emailService, never()).sendAppointmentReminderEmail(any(Appointment.class));
    }
}
