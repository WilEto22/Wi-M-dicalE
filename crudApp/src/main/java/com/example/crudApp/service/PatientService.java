package com.example.crudApp.service;

import com.example.crudApp.dto.PatientRequest;
import com.example.crudApp.dto.PatientResponse;
import com.example.crudApp.dto.PatientSearchCriteria;
import com.example.crudApp.exception.DuplicateResourceException;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.Patient;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.PatientRepository;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.specification.PatientSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    /**
     * Créer un nouveau patient
     */
    public PatientResponse createPatient(PatientRequest request, String username) {
        logger.info("Création d'un nouveau patient: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (patientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Un patient avec cet email existe déjà");
        }

        // Vérifier si le numéro de téléphone existe déjà
        if (request.getPhoneNumber() != null &&
            patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Un patient avec ce numéro de téléphone existe déjà");
        }

        // Vérifier si le numéro d'assurance existe déjà
        if (request.getInsuranceNumber() != null &&
            patientRepository.findByInsuranceNumber(request.getInsuranceNumber()).isPresent()) {
            throw new DuplicateResourceException("Un patient avec ce numéro d'assurance existe déjà");
        }

        Patient patient = new Patient();
        mapRequestToPatient(request, patient);

        // Associer le patient au médecin connecté
        if (username != null) {
            User doctor = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
            patient.setUser(doctor);
        }

        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient créé avec succès: ID {}", savedPatient.getId());

        return PatientResponse.fromPatient(savedPatient);
    }

    /**
     * Récupérer tous les patients avec pagination
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(int page, int size, String sortBy, String sortDirection) {
        logger.info("Récupération des patients - Page: {}, Size: {}", page, size);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Patient> patientsPage = patientRepository.findAll(pageable);

        return patientsPage.map(PatientResponse::fromPatient);
    }

    /**
     * Récupérer un patient par ID
     */
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        logger.info("Récupération du patient avec ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));

        return PatientResponse.fromPatient(patient);
    }

    /**
     * Mettre à jour un patient
     */
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        logger.info("Mise à jour du patient avec ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));

        // Vérifier si l'email est déjà utilisé par un autre patient
        patientRepository.findByEmail(request.getEmail()).ifPresent(existingPatient -> {
            if (!existingPatient.getId().equals(id)) {
                throw new DuplicateResourceException("Un autre patient utilise déjà cet email");
            }
        });

        // Vérifier si le numéro d'assurance est déjà utilisé par un autre patient
        if (request.getInsuranceNumber() != null) {
            patientRepository.findByInsuranceNumber(request.getInsuranceNumber()).ifPresent(existingPatient -> {
                if (!existingPatient.getId().equals(id)) {
                    throw new DuplicateResourceException("Un autre patient utilise déjà ce numéro d'assurance");
                }
            });
        }

        mapRequestToPatient(request, patient);
        Patient updatedPatient = patientRepository.save(patient);

        logger.info("Patient mis à jour avec succès: ID {}", id);
        return PatientResponse.fromPatient(updatedPatient);
    }

    /**
     * Supprimer un patient
     */
    public void deletePatient(Long id) {
        logger.info("Suppression du patient avec ID: {}", id);

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id);
        }

        patientRepository.deleteById(id);
        logger.info("Patient supprimé avec succès: ID {}", id);
    }

    /**
     * Rechercher des patients avec critères
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> searchPatients(PatientSearchCriteria criteria, int page, int size,
                                                  String sortBy, String sortDirection) {
        logger.info("Recherche de patients avec critères: {}", criteria);

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);

        Page<Patient> patientsPage = patientRepository.findAll(spec, pageable);
        return patientsPage.map(PatientResponse::fromPatient);
    }

    /**
     * Récupérer les patients d'un médecin spécifique
     */
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByDoctor(String username) {
        logger.info("Récupération des patients du médecin: {}", username);

        User doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));

        List<Patient> patients = patientRepository.findByUserAndIsActive(doctor, true);
        return patients.stream()
                .map(PatientResponse::fromPatient)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer les patients nécessitant un suivi (pas de visite depuis X jours)
     */
    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsNeedingFollowUp(int daysAgo) {
        logger.info("Récupération des patients nécessitant un suivi (pas de visite depuis {} jours)", daysAgo);

        LocalDate cutoffDate = LocalDate.now().minusDays(daysAgo);
        List<Patient> patients = patientRepository.findPatientsNeedingFollowUp(cutoffDate);

        return patients.stream()
                .map(PatientResponse::fromPatient)
                .collect(Collectors.toList());
    }

    /**
     * Archiver un patient (le marquer comme inactif)
     */
    public PatientResponse archivePatient(Long id) {
        logger.info("Archivage du patient avec ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));

        patient.setIsActive(false);
        Patient archivedPatient = patientRepository.save(patient);

        logger.info("Patient archivé avec succès: ID {}", id);
        return PatientResponse.fromPatient(archivedPatient);
    }

    /**
     * Réactiver un patient
     */
    public PatientResponse reactivatePatient(Long id) {
        logger.info("Réactivation du patient avec ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé avec l'ID: " + id));

        patient.setIsActive(true);
        Patient reactivatedPatient = patientRepository.save(patient);

        logger.info("Patient réactivé avec succès: ID {}", id);
        return PatientResponse.fromPatient(reactivatedPatient);
    }

    /**
     * Mapper PatientRequest vers Patient
     */
    private void mapRequestToPatient(PatientRequest request, Patient patient) {
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setAge(request.getAge());
        patient.setAddress(request.getAddress());
        patient.setBloodType(request.getBloodType());
        patient.setAllergies(request.getAllergies());
        patient.setMedicalHistory(request.getMedicalHistory());
        patient.setLastVisit(request.getLastVisit());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        patient.setInsuranceNumber(request.getInsuranceNumber());
        patient.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
    }
}
