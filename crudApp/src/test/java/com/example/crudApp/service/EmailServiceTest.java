package com.example.crudApp.service;

import com.example.crudApp.model.*;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour EmailService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private Appointment testAppointment;
    private User testPatient;
    private User testDoctor;

    @BeforeEach
    void setUp() {
        // Pas besoin de configurer fromEmail et appName car ils sont gérés par le service

        // Créer un médecin de test
        testDoctor = User.builder()
                .id(1L)
                .username("doctor@example.com")
                .fullName("Dr. Jean Dupont")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .roles("ROLE_USER")
                .build();

        // Créer un patient de test
        testPatient = User.builder()
                .id(2L)
                .username("patient@example.com")
                .fullName("Marie Martin")
                .userType(UserType.PATIENT)
                .phoneNumber("0612345678")
                .dateOfBirth(LocalDate.of(1988, 5, 15))
                .roles("ROLE_USER")
                .build();

        // Créer un rendez-vous de test
        testAppointment = Appointment.builder()
                .id(100L)
                .doctor(testDoctor)
                .patient(testPatient)
                .appointmentDateTime(LocalDateTime.now().plusDays(2))
                .reason("Consultation de routine")
                .status(AppointmentStatus.PENDING)
                .build();

        // Mock du JavaMailSender
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void testSendAppointmentConfirmationEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentConfirmationEmail(testAppointment);

        // Assert - La méthode async a été appelée sans erreur
        // Note: Les méthodes @Async ne s'exécutent pas dans les tests unitaires sans contexte Spring
    }

    @Test
    void testSendAppointmentConfirmationEmail_PatientWithoutEmail() {
        // Arrange
        testPatient = User.builder()
                .id(2L)
                .username(null)
                .fullName("Marie Martin")
                .userType(UserType.PATIENT)
                .roles("ROLE_USER")
                .build();
        testAppointment.setPatient(testPatient);

        // Act - Ne doit pas lever d'exception même si le patient n'a pas d'email
        emailService.sendAppointmentConfirmationEmail(testAppointment);

        // Assert - Pas d'exception levée
    }

    @Test
    void testSendAppointmentNotificationToDoctor_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentNotificationToDoctor(testAppointment);

        // Assert - La méthode async a été appelée sans erreur
    }

    @Test
    void testSendAppointmentStatusUpdateEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));
        testAppointment.setStatus(AppointmentStatus.CONFIRMED);

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentStatusUpdateEmail(testAppointment, "PENDING");

        // Assert - La méthode async a été appelée sans erreur
    }

    @Test
    void testSendAppointmentReminderEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentReminderEmail(testAppointment);

        // Assert - La méthode async a été appelée sans erreur
    }

    @Test
    void testSendAppointmentConfirmationEmail_EmailSendingFails() {
        // Arrange
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        // Act - Ne doit pas lever d'exception même en cas d'erreur SMTP
        emailService.sendAppointmentConfirmationEmail(testAppointment);

        // Assert - L'erreur est gérée en interne et loggée
    }

    @Test
    void testSendAppointmentStatusUpdateEmail_CancelledStatus() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));
        testAppointment.setStatus(AppointmentStatus.CANCELLED);

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentStatusUpdateEmail(testAppointment, "CONFIRMED");

        // Assert - La méthode async a été appelée sans erreur
    }

    @Test
    void testSendAppointmentStatusUpdateEmail_CompletedStatus() {
        // Arrange
        doNothing().when(mailSender).send(any(MimeMessage.class));
        testAppointment.setStatus(AppointmentStatus.COMPLETED);

        // Act - Ne doit pas lever d'exception
        emailService.sendAppointmentStatusUpdateEmail(testAppointment, "CONFIRMED");

        // Assert - La méthode async a été appelée sans erreur
    }
}
