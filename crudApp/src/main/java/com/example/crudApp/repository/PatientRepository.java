package com.example.crudApp.repository;

import com.example.crudApp.model.Patient;
import com.example.crudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {

    // Recherche par email (hérité de Person)
    Optional<Patient> findByEmail(String email);

    // Recherche par numéro de téléphone
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    // Vérifier l'existence par email
    boolean existsByEmail(String email);

    // Vérifier l'existence par numéro de téléphone
    boolean existsByPhoneNumber(String phoneNumber);

    // Recherche par groupe sanguin
    List<Patient> findByBloodType(String bloodType);

    // Recherche par utilisateur (médecin)
    List<Patient> findByUser(User user);

    // Recherche par utilisateur et statut actif
    List<Patient> findByUserAndIsActive(User user, Boolean isActive);

    // Recherche des patients actifs
    List<Patient> findByIsActive(Boolean isActive);

    // Recherche par nom (case insensitive)
    List<Patient> findByNameContainingIgnoreCase(String name);

    // Recherche des patients avec visite récente
    @Query("SELECT p FROM Patient p WHERE p.lastVisit >= :date AND p.isActive = true")
    List<Patient> findPatientsWithRecentVisit(@Param("date") LocalDate date);

    // Recherche des patients sans visite récente
    @Query("SELECT p FROM Patient p WHERE p.lastVisit < :date AND p.isActive = true")
    List<Patient> findPatientsNeedingFollowUp(@Param("date") LocalDate date);

    // Recherche par allergie spécifique
    @Query("SELECT p FROM Patient p WHERE LOWER(p.allergies) LIKE LOWER(CONCAT('%', :allergy, '%')) AND p.isActive = true")
    List<Patient> findByAllergy(@Param("allergy") String allergy);

    // Compter les patients actifs par médecin
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.user = :user AND p.isActive = true")
    Long countActivePatientsByUser(@Param("user") User user);

    // Recherche par numéro d'assurance
    Optional<Patient> findByInsuranceNumber(String insuranceNumber);

    // Recherche des patients par spécialité du médecin
    @Query("SELECT p FROM Patient p WHERE p.user.specialty = :specialty AND p.isActive = true")
    List<Patient> findByDoctorSpecialty(@Param("specialty") com.example.crudApp.model.MedicalSpecialty specialty);

    // Compter les patients par spécialité
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.user.specialty = :specialty AND p.isActive = true")
    Long countPatientsBySpecialty(@Param("specialty") com.example.crudApp.model.MedicalSpecialty specialty);
}
