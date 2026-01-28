package com.example.crudApp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityExceptionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidAvailabilityExceptionRequest() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");
        request.setIsAvailable(false);

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testExceptionDateIsRequired() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setReason("Vacances");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La date est obligatoire")))
                .isTrue();
    }

    @Test
    void testExceptionDateMustBeFutureOrPresent() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().minusDays(1));
        request.setReason("Vacances");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("futur")))
                .isTrue();
    }

    @Test
    void testReasonIsRequired() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("La raison est obligatoire")))
                .isTrue();
    }

    @Test
    void testReasonMinLength() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("V");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("entre 2 et 255 caractères")))
                .isTrue();
    }

    @Test
    void testReasonMaxLength() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("a".repeat(256));

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("entre 2 et 255 caractères")))
                .isTrue();
    }

    @Test
    void testValidStartTimeFormat() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");
        request.setStartTime("09:00:00");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidStartTimeFormat() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");
        request.setStartTime("9:00");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("HH:mm:ss")))
                .isTrue();
    }

    @Test
    void testValidEndTimeFormat() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");
        request.setEndTime("17:00:00");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidEndTimeFormat() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");
        request.setEndTime("25:00:00");

        // When
        Set<ConstraintViolation<AvailabilityExceptionRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("HH:mm:ss")))
                .isTrue();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();

        // Then
        assertThat(request).isNotNull();
        assertThat(request.getIsAvailable()).isFalse();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);

        // When
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest(
                date,
                "Vacances",
                false,
                "09:00:00",
                "17:00:00"
        );

        // Then
        assertThat(request.getExceptionDate()).isEqualTo(date);
        assertThat(request.getReason()).isEqualTo("Vacances");
        assertThat(request.getIsAvailable()).isFalse();
        assertThat(request.getStartTime()).isEqualTo("09:00:00");
        assertThat(request.getEndTime()).isEqualTo("17:00:00");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        LocalDate date = LocalDate.now().plusDays(1);

        // When
        request.setExceptionDate(date);
        request.setReason("Vacances");
        request.setIsAvailable(true);
        request.setStartTime("09:00:00");
        request.setEndTime("17:00:00");

        // Then
        assertThat(request.getExceptionDate()).isEqualTo(date);
        assertThat(request.getReason()).isEqualTo("Vacances");
        assertThat(request.getIsAvailable()).isTrue();
        assertThat(request.getStartTime()).isEqualTo("09:00:00");
        assertThat(request.getEndTime()).isEqualTo("17:00:00");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate date = LocalDate.now().plusDays(1);

        AvailabilityExceptionRequest request1 = new AvailabilityExceptionRequest();
        request1.setExceptionDate(date);
        request1.setReason("Vacances");

        AvailabilityExceptionRequest request2 = new AvailabilityExceptionRequest();
        request2.setExceptionDate(date);
        request2.setReason("Vacances");

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AvailabilityExceptionRequest request = new AvailabilityExceptionRequest();
        request.setExceptionDate(LocalDate.now().plusDays(1));
        request.setReason("Vacances");

        // When
        String result = request.toString();

        // Then
        assertThat(result).contains("Vacances");
    }
}
