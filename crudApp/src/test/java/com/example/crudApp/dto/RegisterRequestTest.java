package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.UserType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDoctorRegistration() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("dr.martin");
        request.setEmail("dr.martin@hospital.com");
        request.setPassword("password123");
        request.setUserType(UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidPatientRegistration() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("patient123");
        request.setEmail("patient@example.com");
        request.setPassword("password123");
        request.setUserType(UserType.PATIENT);
        request.setFullName("Jean Dupont");
        request.setPhoneNumber("+33612345678");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testUsernameIsRequired() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserType(UserType.PATIENT);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le nom d'utilisateur est obligatoire")))
                .isTrue();
    }

    @Test
    void testEmailIsRequired() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setUserType(UserType.PATIENT);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email est obligatoire")))
                .isTrue();
    }

    @Test
    void testPasswordIsRequired() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setUserType(UserType.PATIENT);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le mot de passe est obligatoire")))
                .isTrue();
    }

    @Test
    void testUserTypeIsRequired() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le type d'utilisateur est obligatoire")))
                .isTrue();
    }

    @Test
    void testDateOfBirthMustBeInPast() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserType(UserType.PATIENT);
        request.setDateOfBirth(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("passé")))
                .isTrue();
    }

    @Test
    void testUsernameMinLength() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ab");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserType(UserType.PATIENT);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("entre 3 et 50 caractères")))
                .isTrue();
    }

    @Test
    void testPasswordMinLength() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("12345");
        request.setUserType(UserType.PATIENT);

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("au moins 6 caractères")))
                .isTrue();
    }

    @Test
    void testGettersAndSetters() {
        // Given
        RegisterRequest request = new RegisterRequest();

        // When
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUserType(UserType.DOCTOR);
        request.setSpecialty(MedicalSpecialty.CARDIOLOGIE);
        request.setFullName("Jean Dupont");
        request.setPhoneNumber("+33612345678");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));

        // Then
        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getEmail()).isEqualTo("test@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
        assertThat(request.getUserType()).isEqualTo(UserType.DOCTOR);
        assertThat(request.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(request.getFullName()).isEqualTo("Jean Dupont");
        assertThat(request.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(request.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        RegisterRequest request1 = new RegisterRequest();
        request1.setUsername("testuser");
        request1.setEmail("test@example.com");
        request1.setPassword("password123");
        request1.setUserType(UserType.PATIENT);

        RegisterRequest request2 = new RegisterRequest();
        request2.setUsername("testuser");
        request2.setEmail("test@example.com");
        request2.setPassword("password123");
        request2.setUserType(UserType.PATIENT);

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setUserType(UserType.DOCTOR);

        // When
        String result = request.toString();

        // Then
        assertThat(result).contains("testuser");
        assertThat(result).contains("test@example.com");
    }
}
