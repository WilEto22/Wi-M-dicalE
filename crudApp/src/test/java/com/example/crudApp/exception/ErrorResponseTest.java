package com.example.crudApp.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testAllArgsConstructor() {
        // Given
        Map<String, String> errors = new HashMap<>();
        errors.put("field1", "error1");
        errors.put("field2", "error2");
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad Request", errors, timestamp);

        // Then
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getMessage()).isEqualTo("Bad Request");
        assertThat(errorResponse.getErrors()).hasSize(2);
        assertThat(errorResponse.getErrors()).containsEntry("field1", "error1");
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse(0, null, null, null);
        Map<String, String> errors = new HashMap<>();
        errors.put("username", "Username is required");
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        errorResponse.setStatus(404);
        errorResponse.setMessage("Not Found");
        errorResponse.setErrors(errors);
        errorResponse.setTimestamp(timestamp);

        // Then
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getMessage()).isEqualTo("Not Found");
        assertThat(errorResponse.getErrors()).containsEntry("username", "Username is required");
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");

        ErrorResponse response1 = new ErrorResponse(400, "Bad Request", errors, timestamp);
        ErrorResponse response2 = new ErrorResponse(400, "Bad Request", errors, timestamp);

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error");
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", errors, timestamp);

        // When
        String result = errorResponse.toString();

        // Then
        assertThat(result).contains("500");
        assertThat(result).contains("Internal Server Error");
    }

    @Test
    void testWithNullErrors() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found", null, timestamp);

        // Then
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getMessage()).isEqualTo("Not Found");
        assertThat(errorResponse.getErrors()).isNull();
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    }

    @Test
    void testWithEmptyErrors() {
        // Given
        Map<String, String> errors = new HashMap<>();
        LocalDateTime timestamp = LocalDateTime.now();

        // When
        ErrorResponse errorResponse = new ErrorResponse(200, "OK", errors, timestamp);

        // Then
        assertThat(errorResponse.getErrors()).isEmpty();
    }
}
