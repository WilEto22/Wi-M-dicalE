package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSearchCriteria {

    // Critères de recherche de base (hérités de Person)
    private String name;
    private String email;
    private Integer minAge;
    private Integer maxAge;
    private String address;

    // Critères de recherche médicaux
    private String bloodType;
    private String allergy; // Recherche dans les allergies
    private LocalDate lastVisitAfter; // Patients vus après cette date
    private LocalDate lastVisitBefore; // Patients vus avant cette date
    private Boolean isActive; // Patients actifs ou archivés
    private String insuranceNumber;
    private String phoneNumber;
    private String emergencyContact;

    // Critère de recherche par spécialité du médecin
    private MedicalSpecialty doctorSpecialty; // Filtrer par spécialité du médecin
}
