package com.example.crudApp.exception;

public class AppointmentModificationNotAllowedException extends RuntimeException {
    public AppointmentModificationNotAllowedException(String message) {
        super(message);
    }
}
