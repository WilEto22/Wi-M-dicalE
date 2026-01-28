package com.example.crudApp.service;

import com.example.crudApp.dto.AppointmentRequest;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.exception.ServiceException;
import com.example.crudApp.exception.TokenRefreshException;
import com.example.crudApp.model.*;
import com.example.crudApp.repository.AppointmentRepository;
import com.example.crudApp.repository.DoctorAvailabilityRepository;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    @Mock
    private DoctorAvailabilityExceptionService doctorAvailabilityExceptionService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentService appointmentService;

    private User patient;
    private User doctor;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        patient = new User();
        patient.setId(1L);
        patient.setUsername("patient1");
        patient.setEmail("patient1@test.com");
        patient.setUserType(UserType.PATIENT);
        patient.setFullName("Patient Test");

        doctor = new User();
        doctor.setId(2L);
        doctor.setUsername("doctor1");
        doctor.setEmail("doctor1@test.com");
        doctor.setUserType(UserType.DOCTOR);
        doctor.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(LocalDateTime.now().plusDays(2));
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setReason("Consultation");
    }

    @Test
    void testCreateAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(2L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(2).withHour(10).withMinute(0));
        request.setReason("Consultation");

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getAppointmentDateTime().getDayOfWeek());
        availability.setStartTime(java.time.LocalTime.of(9, 0));
        availability.setEndTime(java.time.LocalTime.of(17, 0));
        availability.setSlotDurationMinutes(30);
        availability.setIsActive(true);

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityRepository.findByDoctorAndDayOfWeekAndIsActiveTrue(
                eq(doctor), any(java.time.DayOfWeek.class)))
                .thenReturn(Arrays.asList(availability));
        when(doctorAvailabilityExceptionService.getExceptionForDate(any(), any())).thenReturn(null);
        when(appointmentRepository.countConflictingAppointments(any(), any())).thenReturn(0L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.createAppointment(request, "patient1");

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(emailService, times(1)).sendAppointmentConfirmationEmail(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_PatientNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(2L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(2));

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.createAppointment(request, "patient1");
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_DoctorNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(999L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(2));

        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(patient));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.createAppointment(request, "patient1");
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void testGetPatientAppointments_Success() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(userRepository.findByUsername("patient1")).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getPatientAppointments("patient1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByPatient(patient);
    }

    @Test
    void testGetDoctorAppointments_Success() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getDoctorAppointments("doctor1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByDoctor(doctor);
    }

    @Test
    void testCancelAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.cancelAppointment(1L, "patient1");

        assertNotNull(result);
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());
        verify(emailService, times(1)).sendAppointmentStatusUpdateEmail(any(Appointment.class), anyString());
    }

    @Test
    void testCancelAppointment_NotFound() {
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.cancelAppointment(999L, "patient1");
        });
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Appointment not found");
        assertEquals("Appointment not found", exception.getMessage());
    }

    @Test
    void testServiceException() {
        ServiceException exception = new ServiceException("Service error");
        assertEquals("Service error", exception.getMessage());
    }

    @Test
    void testServiceExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        ServiceException exception = new ServiceException("Service error", cause);

        assertEquals("Service error", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testTokenRefreshException() {
        TokenRefreshException exception = new TokenRefreshException("Token expired");
        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void testTokenRefreshExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        TokenRefreshException exception = new TokenRefreshException("Token expired", cause);

        assertEquals("Token expired", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
