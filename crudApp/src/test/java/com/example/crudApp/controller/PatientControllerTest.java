package com.example.crudApp.controller;

import com.example.crudApp.config.TestSecurityConfig;
import com.example.crudApp.dto.PatientRequest;
import com.example.crudApp.model.Patient;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.PatientRepository;
import com.example.crudApp.security.JwtUtil;
import com.example.crudApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private String jwtToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        // No need to delete all - @Transactional will rollback after each test

        // Create a test user and generate JWT token
        testUser = User.builder()
                .username("doctor")
                .password("password123")
                .roles("ROLE_USER")
                .userType(com.example.crudApp.model.UserType.DOCTOR)
                .specialty(com.example.crudApp.model.MedicalSpecialty.CARDIOLOGIE)
                .build();
        userService.saveNewUser(testUser);
        jwtToken = jwtUtil.generateToken("doctor");
    }

    @Test
    void createPatient_ShouldReturnCreatedPatient_WhenValidRequest() throws Exception {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("jean.dupont@example.com");
        request.setAge(45);
        request.setAddress("123 Rue de la Santé, Paris");
        request.setBloodType("A+");
        request.setAllergiesList(List.of("Pénicilline", "Pollen"));
        request.setPhoneNumber("+33612345678");
        request.setEmergencyContact("Marie Dupont");
        request.setEmergencyPhone("+33687654321");
        request.setInsuranceNumber("1234567890123");
        request.setLastVisit(LocalDate.now().minusDays(10));

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jean Dupont"))
                .andExpect(jsonPath("$.email").value("jean.dupont@example.com"))
                .andExpect(jsonPath("$.age").value(45))
                .andExpect(jsonPath("$.bloodType").value("A+"))
                .andExpect(jsonPath("$.allergies").value("Pénicilline, Pollen"))
                .andExpect(jsonPath("$.phoneNumber").value("+33612345678"))
                .andExpect(jsonPath("$.insuranceNumber").value("1234567890123"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.doctorUsername").value("doctor"));
    }

    @Test
    void createPatient_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("invalid-email");
        request.setAge(45);
        request.setAddress("123 Rue de la Santé, Paris");

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPatient_ShouldReturnBadRequest_WhenInvalidBloodType() throws Exception {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("jean@example.com");
        request.setAge(45);
        request.setAddress("123 Rue de la Santé, Paris");
        request.setBloodType("Z+"); // Invalid blood type

        // When & Then
        mockMvc.perform(post("/api/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPatients_ShouldReturnPaginatedPatients() throws Exception {
        // Given
        Patient patient1 = new Patient();
        patient1.setName("Alice Martin");
        patient1.setEmail("alice@example.com");
        patient1.setAge(30);
        patient1.setAddress("Paris");
        patient1.setBloodType("O+");
        patient1.setIsActive(true);
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setName("Bob Durand");
        patient2.setEmail("bob@example.com");
        patient2.setAge(50);
        patient2.setAddress("Lyon");
        patient2.setBloodType("A-");
        patient2.setIsActive(true);
        patientRepository.save(patient2);

        // When & Then
        mockMvc.perform(get("/api/patients")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.patients").isArray())
                .andExpect(jsonPath("$.patients", hasSize(2)))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getPatientById_ShouldReturnPatient_WhenPatientExists() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setBloodType("A+");
        patient.setIsActive(true);
        Patient savedPatient = patientRepository.save(patient);

        // When & Then
        mockMvc.perform(get("/api/patients/" + savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPatient.getId()))
                .andExpect(jsonPath("$.name").value("Jean Dupont"))
                .andExpect(jsonPath("$.email").value("jean@example.com"))
                .andExpect(jsonPath("$.bloodType").value("A+"));
    }

    @Test
    void getPatientById_ShouldReturnNotFound_WhenPatientDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/patients/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePatient_ShouldReturnUpdatedPatient_WhenValidRequest() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setBloodType("A+");
        patient.setIsActive(true);
        Patient savedPatient = patientRepository.save(patient);

        PatientRequest updateRequest = new PatientRequest();
        updateRequest.setName("Jean Dupont");
        updateRequest.setEmail("jean.updated@example.com");
        updateRequest.setAge(46);
        updateRequest.setAddress("Lyon");
        updateRequest.setBloodType("A+");
        updateRequest.setAllergiesList(List.of("Aspirine"));

        // When & Then
        mockMvc.perform(put("/api/patients/" + savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jean.updated@example.com"))
                .andExpect(jsonPath("$.age").value(46))
                .andExpect(jsonPath("$.address").value("Lyon"))
                .andExpect(jsonPath("$.allergies").value("Aspirine"));
    }

    @Test
    void deletePatient_ShouldReturnSuccess_WhenPatientExists() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setIsActive(true);
        Patient savedPatient = patientRepository.save(patient);

        // When & Then
        mockMvc.perform(delete("/api/patients/" + savedPatient.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Patient supprimé avec succès"));
    }

    @Test
    void searchPatients_ShouldReturnFilteredPatients_WhenSearchByBloodType() throws Exception {
        // Given
        Patient patient1 = new Patient();
        patient1.setName("Alice Martin");
        patient1.setEmail("alice@example.com");
        patient1.setAge(30);
        patient1.setAddress("Paris");
        patient1.setBloodType("O+");
        patient1.setIsActive(true);
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setName("Bob Durand");
        patient2.setEmail("bob@example.com");
        patient2.setAge(50);
        patient2.setAddress("Lyon");
        patient2.setBloodType("A+");
        patient2.setIsActive(true);
        patientRepository.save(patient2);

        // When & Then
        mockMvc.perform(get("/api/patients/search")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("bloodType", "O+"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.patients[0].name").value("Alice Martin"))
                .andExpect(jsonPath("$.patients[0].bloodType").value("O+"));
    }

    @Test
    void searchPatients_ShouldReturnFilteredPatients_WhenSearchByAllergy() throws Exception {
        // Given
        Patient patient1 = new Patient();
        patient1.setName("Alice Martin");
        patient1.setEmail("alice@example.com");
        patient1.setAge(30);
        patient1.setAddress("Paris");
        patient1.setAllergies("Pénicilline, Pollen");
        patient1.setIsActive(true);
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setName("Bob Durand");
        patient2.setEmail("bob@example.com");
        patient2.setAge(50);
        patient2.setAddress("Lyon");
        patient2.setAllergies("Aspirine");
        patient2.setIsActive(true);
        patientRepository.save(patient2);

        // When & Then
        mockMvc.perform(get("/api/patients/search")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("allergy", "Pénicilline"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.patients[0].name").value("Alice Martin"));
    }

    @Test
    void archivePatient_ShouldMarkPatientAsInactive() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setIsActive(true);
        Patient savedPatient = patientRepository.save(patient);

        // When & Then
        mockMvc.perform(put("/api/patients/" + savedPatient.getId() + "/archive")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    void reactivatePatient_ShouldMarkPatientAsActive() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setIsActive(false);
        Patient savedPatient = patientRepository.save(patient);

        // When & Then
        mockMvc.perform(put("/api/patients/" + savedPatient.getId() + "/reactivate")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void exportPatientsToCSV_ShouldReturnCSVFile() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setBloodType("A+");
        patient.setIsActive(true);
        patientRepository.save(patient);

        // When & Then
        mockMvc.perform(get("/api/patients/export/csv")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"patients.csv\""));
    }

    @Test
    void exportPatientsToExcel_ShouldReturnExcelFile() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setBloodType("A+");
        patient.setIsActive(true);
        patientRepository.save(patient);

        // When & Then
        mockMvc.perform(get("/api/patients/export/excel")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"patients.xlsx\""));
    }

    @Test
    void exportPatientsToPDF_ShouldReturnPDFFile() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("Paris");
        patient.setBloodType("A+");
        patient.setIsActive(true);
        patientRepository.save(patient);

        // When & Then
        mockMvc.perform(get("/api/patients/export/pdf")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"patients.pdf\""));
    }
}
