package com.example.crudApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String message;

    public AuthResponse(String token) {
        this.token = token;
        this.username = null;
        this.message = null;
    }
}
