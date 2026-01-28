package com.example.crudApp.service;

import com.example.crudApp.dto.PatientRequest;
import com.example.crudApp.dto.PatientResponse;
import com.example.crudApp.dto.PatientSearchCriteria;
import com.example.crudApp.exception.DuplicateResourceException;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.Patient;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.repository.PatientRepository;
import com.example.crudApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private User doctor;
    private PatientRequest patientRequest;

    @BeforeEach
    void setUp() {
        doctor = User.builder()
                .id(1L)
                .username("doctor1")
                .email("doctor1@example.com")
                .userType(UserType.DOCTOR)
                .build();

        patient = Patient.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@test.com")
                .age(35)
                .address("123 Main St")
                .phoneNumber("0123456789")
                .isActive(true)
                .user(doctor)
                .build();

        patientRequest = new PatientRequest();
        patientRequest.setName("Jane Smith");
        patientRequest.setEmail("jane.smith@test.com");
        patientRequest.setAge(28);
        patientRequest.setAddress("456 Oak Ave");
        patientRequest.setPhoneNumber("0987654321");
    }

    @Test
    void testCreatePatient_Success() {
        // Given
        when(patientRepository.findByEmail(patientRequest.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.existsByPhoneNumber(patientRequest.getPhoneNumber())).thenReturn(false);
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        PatientResponse result = patientService.createPatient(patientRequest, "doctor1");

        // Then
        assertThat(result).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testCreatePatient_DuplicateEmail() {
        // Given
        when(patientRepository.findByEmail(patientRequest.getEmail())).thenReturn(Optional.of(patient));

        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patientRequest, "doctor1"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email existe déjà");

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testCreatePatient_DuplicatePhoneNumber() {
        // Given
        when(patientRepository.findByEmail(patientRequest.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.existsByPhoneNumber(patientRequest.getPhoneNumber())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patientRequest, "doctor1"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("numéro de téléphone existe déjà");
    }

    @Test
    void testCreatePatient_DuplicateInsuranceNumber() {
        // Given
        patientRequest.setInsuranceNumber("INS123");
        when(patientRepository.findByEmail(patientRequest.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.existsByPhoneNumber(patientRequest.getPhoneNumber())).thenReturn(false);
        when(patientRepository.findByInsuranceNumber("INS123")).thenReturn(Optional.of(patient));

        // When & Then
        assertThatThrownBy(() -> patientService.createPatient(patientRequest, "doctor1"))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("numéro d'assurance existe déjà");
    }

    @Test
    void testGetPatientById_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // When
        PatientResponse result = patientService.getPatientById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
    }

    @Test
    void testGetPatientById_NotFound() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.getPatientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Patient non trouvé");
    }

    @Test
    void testUpdatePatient_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.findByEmail(patientRequest.getEmail())).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        PatientResponse result = patientService.updatePatient(1L, patientRequest);

        // Then
        assertThat(result).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient_NotFound() {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> patientService.updatePatient(999L, patientRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testDeletePatient_Success() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);

        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository).deleteById(1L);
    }

    @Test
    void testDeletePatient_NotFound() {
        // Given
        when(patientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> patientService.deletePatient(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(patientRepository, never()).deleteById(any());
    }

    @Test
    void testGetAllPatients_Success() {
        // Given
        List<Patient> patients = Arrays.asList(patient);
        Page<Patient> patientPage = new PageImpl<>(patients);
        when(patientRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(patientPage);

        // When
        Page<PatientResponse> result = patientService.getAllPatients(0, 10, "name", "asc");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testSearchPatients_Success() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName("John");
        List<Patient> patients = Arrays.asList(patient);
        Page<Patient> patientPage = new PageImpl<>(patients);
        when(patientRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(patientPage);

        // When
        Page<PatientResponse> result = patientService.searchPatients(criteria, 0, 10, "name", "asc");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetPatientsByDoctor_Success() {
        // Given
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctor));
        when(patientRepository.findByUserAndIsActive(doctor, true)).thenReturn(Arrays.asList(patient));

        // When
        List<PatientResponse> result = patientService.getPatientsByDoctor("doctor1");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetPatientsNeedingFollowUp_Success() {
        // Given
        LocalDate cutoffDate = LocalDate.now().minusDays(90);
        when(patientRepository.findPatientsNeedingFollowUp(any(LocalDate.class)))
                .thenReturn(Arrays.asList(patient));

        // When
        List<PatientResponse> result = patientService.getPatientsNeedingFollowUp(90);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void testArchivePatient_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        PatientResponse result = patientService.archivePatient(1L);

        // Then
        assertThat(result).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testReactivatePatient_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        PatientResponse result = patientService.reactivatePatient(1L);

        // Then
        assertThat(result).isNotNull();
        verify(patientRepository).save(any(Patient.class));
    }
}
