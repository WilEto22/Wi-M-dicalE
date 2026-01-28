package com.example.crudApp.controller;

import com.example.crudApp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorControllerTest {

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Doctor not found");

        assertEquals("Doctor not found", exception.getMessage());
    }
}
