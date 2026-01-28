package com.example.crudApp.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void testHandleDuplicateResourceException() {
        DuplicateResourceException exception = new DuplicateResourceException("Duplicate resource");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResourceException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate resource", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void testHandleTokenRefreshException() {
        TokenRefreshException exception = new TokenRefreshException("Token expired");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenRefreshException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Token expired", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void testHandleServiceException() {
        ServiceException exception = new ServiceException("Service error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleServiceException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service error", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void testHandleBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nom d'utilisateur ou mot de passe incorrect", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void testHandleUsernameNotFoundException() {
        UsernameNotFoundException exception = new UsernameNotFoundException("User not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUsernameNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void testHandleAppointmentModificationNotAllowedException() {
        AppointmentModificationNotAllowedException exception =
            new AppointmentModificationNotAllowedException("Cannot modify appointment");

        ResponseEntity<ErrorResponse> response =
            exceptionHandler.handleAppointmentModificationNotAllowedException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cannot modify appointment", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "error message");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erreur de validation", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getErrors());
    }

    @Test
    void testHandleGlobalException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Une erreur interne s'est produite", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }
}
