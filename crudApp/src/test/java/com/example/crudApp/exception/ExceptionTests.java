package com.example.crudApp.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for all custom exception classes
 */
class ExceptionTests {

    @Test
    void testResourceNotFoundException() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Resource not found");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testDuplicateResourceException() {
        // When
        DuplicateResourceException exception = new DuplicateResourceException("Duplicate resource");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Duplicate resource");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testServiceException_WithMessage() {
        // When
        ServiceException exception = new ServiceException("Service error occurred");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Service error occurred");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testServiceException_WithMessageAndCause() {
        // Given
        Throwable cause = new IllegalArgumentException("Invalid argument");

        // When
        ServiceException exception = new ServiceException("Service error", cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Service error");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Invalid argument");
    }

    @Test
    void testTokenRefreshException_WithMessage() {
        // When
        TokenRefreshException exception = new TokenRefreshException("Token expired");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Token expired");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testTokenRefreshException_WithMessageAndCause() {
        // Given
        Throwable cause = new IllegalStateException("Invalid token state");

        // When
        TokenRefreshException exception = new TokenRefreshException("Token refresh failed", cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Token refresh failed");
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getCause().getMessage()).isEqualTo("Invalid token state");
    }

    @Test
    void testAppointmentModificationNotAllowedException() {
        // When
        AppointmentModificationNotAllowedException exception =
                new AppointmentModificationNotAllowedException("Cannot modify appointment");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Cannot modify appointment");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testExceptionInheritance() {
        // Verify all custom exceptions extend RuntimeException
        assertThat(new ResourceNotFoundException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new DuplicateResourceException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new ServiceException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new TokenRefreshException("test")).isInstanceOf(RuntimeException.class);
        assertThat(new AppointmentModificationNotAllowedException("test")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testExceptionMessagesArePreserved() {
        // Given
        String message1 = "Resource with ID 123 not found";
        String message2 = "Email already exists";
        String message3 = "Database connection failed";
        String message4 = "Refresh token has expired";
        String message5 = "Appointment is in the past";

        // When
        ResourceNotFoundException ex1 = new ResourceNotFoundException(message1);
        DuplicateResourceException ex2 = new DuplicateResourceException(message2);
        ServiceException ex3 = new ServiceException(message3);
        TokenRefreshException ex4 = new TokenRefreshException(message4);
        AppointmentModificationNotAllowedException ex5 = new AppointmentModificationNotAllowedException(message5);

        // Then
        assertThat(ex1.getMessage()).isEqualTo(message1);
        assertThat(ex2.getMessage()).isEqualTo(message2);
        assertThat(ex3.getMessage()).isEqualTo(message3);
        assertThat(ex4.getMessage()).isEqualTo(message4);
        assertThat(ex5.getMessage()).isEqualTo(message5);
    }

    @Test
    void testExceptionCauseChaining() {
        // Given
        Exception rootCause = new IllegalArgumentException("Root cause");
        Exception intermediateCause = new ServiceException("Intermediate", rootCause);

        // When
        TokenRefreshException finalException = new TokenRefreshException("Final error", intermediateCause);

        // Then
        assertThat(finalException.getCause()).isEqualTo(intermediateCause);
        assertThat(finalException.getCause().getCause()).isEqualTo(rootCause);
    }
}
