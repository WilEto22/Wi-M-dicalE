package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;
    private String username;
    private String fullName;
    private MedicalSpecialty specialty;
    private String specialtyDisplay;

    public static DoctorResponse fromUser(User user) {
        return DoctorResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .specialty(user.getSpecialty())
                .specialtyDisplay(user.getSpecialty() != null ? user.getSpecialty().getDisplayName() : null)
                .build();
    }
}
