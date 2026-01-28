package com.example.crudApp.controller;

import com.example.crudApp.dto.PatientRequest;
import com.example.crudApp.dto.PatientResponse;
import com.example.crudApp.dto.PatientSearchCriteria;
import com.example.crudApp.model.Patient;
import com.example.crudApp.repository.PatientRepository;
import com.example.crudApp.service.ExportService;
import com.example.crudApp.service.PatientService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "🏥 Patients", description = "API de gestion des patients médicaux avec informations médicales complètes")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final ExportService exportService;

    /**
     * Créer un nouveau patient
     * POST /api/patients
     */
    @Operation(
            summary = "Créer un nouveau patient",
            description = "Enregistre un nouveau patient avec ses informations médicales. Le patient est automatiquement associé au médecin connecté."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient créé avec succès",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides (email invalide, groupe sanguin incorrect, etc.)"),
            @ApiResponse(responseCode = "401", description = "Non authentifié - Token JWT manquant ou invalide"),
            @ApiResponse(responseCode = "409", description = "Conflit - Email ou numéro d'assurance déjà utilisé")
    })
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(
            @Parameter(description = "Informations du patient à créer", required = true)
            @Valid @RequestBody PatientRequest patientRequest) {
        logger.info("Requête de création de patient reçue: {}", patientRequest.getEmail());

        String username = getCurrentUsername();
        PatientResponse response = patientService.createPatient(patientRequest, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer tous les patients avec pagination
     * GET /api/patients?page=0&size=10&sortBy=name&sortDirection=asc
     */
    @Operation(
            summary = "Liste paginée des patients",
            description = "Récupère tous les patients avec pagination et tri configurable"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPatients(
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri (name, age, email, lastVisit, etc.)") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri (asc ou desc)") @RequestParam(defaultValue = "asc") String sortDirection) {

        logger.info("Récupération de tous les patients - Page: {}, Size: {}", page, size);

        Page<PatientResponse> patientsPage = patientService.getAllPatients(page, size, sortBy, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("patients", patientsPage.getContent());
        response.put("currentPage", patientsPage.getNumber());
        response.put("totalItems", patientsPage.getTotalElements());
        response.put("totalPages", patientsPage.getTotalPages());
        response.put("hasNext", patientsPage.hasNext());
        response.put("hasPrevious", patientsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer un patient par ID
     * GET /api/patients/{id}
     */
    @Operation(
            summary = "Détails d'un patient",
            description = "Récupère les informations complètes d'un patient par son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(
            @Parameter(description = "ID du patient", required = true) @PathVariable Long id) {
        logger.info("Récupération du patient avec ID: {}", id);

        PatientResponse response = patientService.getPatientById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Mettre à jour un patient
     * PUT /api/patients/{id}
     */
    @Operation(
            summary = "Modifier un patient",
            description = "Met à jour les informations d'un patient existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient modifié avec succès",
                    content = @Content(schema = @Schema(implementation = PatientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email ou numéro d'assurance déjà utilisé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "ID du patient", required = true) @PathVariable Long id,
            @Parameter(description = "Nouvelles informations du patient", required = true) @Valid @RequestBody PatientRequest patientRequest) {

        logger.info("Requête de mise à jour du patient avec ID: {}", id);

        PatientResponse response = patientService.updatePatient(id, patientRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Supprimer un patient
     * DELETE /api/patients/{id}
     */
    @Operation(
            summary = "Supprimer un patient",
            description = "Supprime définitivement un patient de la base de données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePatient(
            @Parameter(description = "ID du patient à supprimer", required = true) @PathVariable Long id) {
        logger.info("Requête de suppression du patient avec ID: {}", id);

        patientService.deletePatient(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient supprimé avec succès");
        response.put("id", id.toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Rechercher des patients avec critères
     * GET /api/patients/search?name=John&bloodType=A+&minAge=20&maxAge=50
     */
    @Operation(
            summary = "Recherche avancée de patients",
            description = "Recherche de patients avec filtres multiples : nom, email, âge, groupe sanguin, allergies, date de visite, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPatients(
            @Parameter(description = "Nom du patient (recherche partielle)") @RequestParam(required = false) String name,
            @Parameter(description = "Email du patient (recherche partielle)") @RequestParam(required = false) String email,
            @Parameter(description = "Âge minimum") @RequestParam(required = false) Integer minAge,
            @Parameter(description = "Âge maximum") @RequestParam(required = false) Integer maxAge,
            @Parameter(description = "Adresse (recherche partielle)") @RequestParam(required = false) String address,
            @Parameter(description = "Groupe sanguin (A+, A-, B+, B-, AB+, AB-, O+, O-)") @RequestParam(required = false) String bloodType,
            @Parameter(description = "Allergie spécifique (recherche dans les allergies)") @RequestParam(required = false) String allergy,
            @Parameter(description = "Date de dernière visite après (format: YYYY-MM-DD)") @RequestParam(required = false) String lastVisitAfter,
            @Parameter(description = "Date de dernière visite avant (format: YYYY-MM-DD)") @RequestParam(required = false) String lastVisitBefore,
            @Parameter(description = "Statut actif (true) ou archivé (false)") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Numéro d'assurance") @RequestParam(required = false) String insuranceNumber,
            @Parameter(description = "Numéro de téléphone") @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Nom du contact d'urgence") @RequestParam(required = false) String emergencyContact,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String sortDirection) {

        logger.info("Recherche de patients avec critères");

        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName(name);
        criteria.setEmail(email);
        criteria.setMinAge(minAge);
        criteria.setMaxAge(maxAge);
        criteria.setAddress(address);
        criteria.setBloodType(bloodType);
        criteria.setAllergy(allergy);
        criteria.setIsActive(isActive);
        criteria.setInsuranceNumber(insuranceNumber);
        criteria.setPhoneNumber(phoneNumber);
        criteria.setEmergencyContact(emergencyContact);

        // Conversion des dates si fournies
        if (lastVisitAfter != null) {
            criteria.setLastVisitAfter(java.time.LocalDate.parse(lastVisitAfter));
        }
        if (lastVisitBefore != null) {
            criteria.setLastVisitBefore(java.time.LocalDate.parse(lastVisitBefore));
        }

        Page<PatientResponse> patientsPage = patientService.searchPatients(criteria, page, size, sortBy, sortDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("patients", patientsPage.getContent());
        response.put("currentPage", patientsPage.getNumber());
        response.put("totalItems", patientsPage.getTotalElements());
        response.put("totalPages", patientsPage.getTotalPages());
        response.put("hasNext", patientsPage.hasNext());
        response.put("hasPrevious", patientsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer les patients du médecin connecté
     * GET /api/patients/my-patients
     */
    @Operation(
            summary = "Mes patients",
            description = "Récupère la liste des patients actifs du médecin connecté"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des patients du médecin"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/my-patients")
    public ResponseEntity<List<PatientResponse>> getMyPatients() {
        String username = getCurrentUsername();
        logger.info("Récupération des patients du médecin: {}", username);

        List<PatientResponse> patients = patientService.getPatientsByDoctor(username);
        return ResponseEntity.ok(patients);
    }

    /**
     * Récupérer les patients nécessitant un suivi
     * GET /api/patients/follow-up?daysAgo=30
     */
    @Operation(
            summary = "Patients nécessitant un suivi",
            description = "Identifie les patients actifs qui n'ont pas eu de visite depuis un certain nombre de jours"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des patients nécessitant un suivi"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/follow-up")
    public ResponseEntity<List<PatientResponse>> getPatientsNeedingFollowUp(
            @Parameter(description = "Nombre de jours depuis la dernière visite", example = "30") @RequestParam(defaultValue = "30") int daysAgo) {

        logger.info("Récupération des patients nécessitant un suivi (pas de visite depuis {} jours)", daysAgo);

        List<PatientResponse> patients = patientService.getPatientsNeedingFollowUp(daysAgo);
        return ResponseEntity.ok(patients);
    }

    /**
     * Archiver un patient (le marquer comme inactif)
     * PUT /api/patients/{id}/archive
     */
    @Operation(
            summary = "Archiver un patient",
            description = "Marque un patient comme inactif sans le supprimer de la base de données"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient archivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/{id}/archive")
    public ResponseEntity<PatientResponse> archivePatient(
            @Parameter(description = "ID du patient à archiver", required = true) @PathVariable Long id) {
        logger.info("Archivage du patient avec ID: {}", id);

        PatientResponse response = patientService.archivePatient(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Réactiver un patient
     * PUT /api/patients/{id}/reactivate
     */
    @Operation(
            summary = "Réactiver un patient",
            description = "Réactive un patient précédemment archivé"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient réactivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/{id}/reactivate")
    public ResponseEntity<PatientResponse> reactivatePatient(
            @Parameter(description = "ID du patient à réactiver", required = true) @PathVariable Long id) {
        logger.info("Réactivation du patient avec ID: {}", id);

        PatientResponse response = patientService.reactivatePatient(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Exporter les patients en CSV
     * GET /api/patients/export/csv
     */
    @Operation(
            summary = "Export CSV des patients",
            description = "Exporte tous les patients au format CSV pour analyse dans Excel/Google Sheets"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier CSV généré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportPatientsToCSV() throws IOException {
        logger.info("Export des patients en CSV");

        List<Patient> patients = patientRepository.findAll();
        String csvContent = exportService.exportPatientsToCSV(patients);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "patients.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.getBytes());
    }

    /**
     * Exporter les patients en Excel
     * GET /api/patients/export/excel
     */
    @Operation(
            summary = "Export Excel des patients",
            description = "Exporte tous les patients au format Excel (.xlsx) avec mise en forme professionnelle"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier Excel généré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportPatientsToExcel() throws IOException {
        logger.info("Export des patients en Excel");

        List<Patient> patients = patientRepository.findAll();
        byte[] excelContent = exportService.exportPatientsToExcel(patients);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "patients.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }

    /**
     * Exporter les patients en PDF
     * GET /api/patients/export/pdf
     */
    @Operation(
            summary = "Export PDF des patients",
            description = "Exporte tous les patients au format PDF avec mention 'Document confidentiel'"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier PDF généré avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPatientsToPDF() throws DocumentException {
        logger.info("Export des patients en PDF");

        List<Patient> patients = patientRepository.findAll();
        byte[] pdfContent = exportService.exportPatientsToPDF(patients);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "patients.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    /**
     * Récupérer le username de l'utilisateur connecté
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
