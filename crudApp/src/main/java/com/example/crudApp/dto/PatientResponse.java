package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String address;
    private String bloodType;
    private String allergies;
    private String medicalHistory;
    private LocalDate lastVisit;
    private String phoneNumber;
    private String emergencyContact;
    private String emergencyPhone;
    private String insuranceNumber;
    private Boolean isActive;
    private String doctorUsername; // Nom du médecin assigné
    private MedicalSpecialty doctorSpecialty; // Spécialité du médecin
    private String doctorSpecialtyDisplay; // Nom d'affichage de la spécialité

    // Méthode statique pour convertir Patient en PatientResponse
    public static PatientResponse fromPatient(Patient patient) {
        MedicalSpecialty specialty = patient.getUser() != null ? patient.getUser().getSpecialty() : null;

        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .email(patient.getEmail())
                .age(patient.getAge())
                .address(patient.getAddress())
                .bloodType(patient.getBloodType())
                .allergies(patient.getAllergies())
                .medicalHistory(patient.getMedicalHistory())
                .lastVisit(patient.getLastVisit())
                .phoneNumber(patient.getPhoneNumber())
                .emergencyContact(patient.getEmergencyContact())
                .emergencyPhone(patient.getEmergencyPhone())
                .insuranceNumber(patient.getInsuranceNumber())
                .isActive(patient.getIsActive())
                .doctorUsername(patient.getUser() != null ? patient.getUser().getUsername() : null)
                .doctorSpecialty(specialty)
                .doctorSpecialtyDisplay(specialty != null ? specialty.getDisplayName() : null)
                .build();
    }
}
