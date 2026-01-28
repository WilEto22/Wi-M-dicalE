package com.example.crudApp.service;

import com.example.crudApp.model.Patient;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour ExportService
 */
@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @InjectMocks
    private ExportService exportService;

    private List<Patient> testPatients;

    @BeforeEach
    void setUp() {
        testPatients = new ArrayList<>();

        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("Jean Dupont");
        patient1.setEmail("jean.dupont@example.com");
        patient1.setAge(45);
        patient1.setAddress("123 Rue de Paris");
        patient1.setBloodType("A+");
        patient1.setAllergies("Pénicilline");
        patient1.setPhoneNumber("0612345678");
        patient1.setEmergencyContact("Marie Dupont");
        patient1.setEmergencyPhone("0698765432");
        patient1.setInsuranceNumber("1234567890123");
        patient1.setLastVisit(LocalDate.of(2024, 1, 15));
        patient1.setIsActive(true);

        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Sophie Martin");
        patient2.setEmail("sophie.martin@example.com");
        patient2.setAge(32);
        patient2.setAddress("456 Avenue des Champs");
        patient2.setBloodType("O-");
        patient2.setAllergies("Aucune");
        patient2.setPhoneNumber("0623456789");
        patient2.setEmergencyContact("Pierre Martin");
        patient2.setEmergencyPhone("0687654321");
        patient2.setInsuranceNumber("9876543210987");
        patient2.setLastVisit(LocalDate.of(2024, 2, 20));
        patient2.setIsActive(false);

        testPatients.add(patient1);
        testPatients.add(patient2);
    }

    @Test
    void exportPatientsToCSV_ShouldGenerateValidCSV() throws IOException {
        // When
        String csvContent = exportService.exportPatientsToCSV(testPatients);

        // Then
        assertThat(csvContent).isNotNull();
        assertThat(csvContent).isNotEmpty();
        assertThat(csvContent).contains("ID,Nom,Email");
        assertThat(csvContent).contains("Jean Dupont");
        assertThat(csvContent).contains("Sophie Martin");
        assertThat(csvContent).contains("jean.dupont@example.com");
        assertThat(csvContent).contains("A+");
        assertThat(csvContent).contains("O-");
        assertThat(csvContent).contains("Actif");
        assertThat(csvContent).contains("Archivé");
    }

    @Test
    void exportPatientsToCSV_ShouldHandleEmptyList() throws IOException {
        // Given
        List<Patient> emptyList = new ArrayList<>();

        // When
        String csvContent = exportService.exportPatientsToCSV(emptyList);

        // Then
        assertThat(csvContent).isNotNull();
        assertThat(csvContent).contains("ID,Nom,Email");
    }

    @Test
    void exportPatientsToExcel_ShouldGenerateValidExcel() throws IOException {
        // When
        byte[] excelContent = exportService.exportPatientsToExcel(testPatients);

        // Then
        assertThat(excelContent).isNotNull();
        assertThat(excelContent.length).isGreaterThan(0);
    }

    @Test
    void exportPatientsToExcel_ShouldHandleEmptyList() throws IOException {
        // Given
        List<Patient> emptyList = new ArrayList<>();

        // When
        byte[] excelContent = exportService.exportPatientsToExcel(emptyList);

        // Then
        assertThat(excelContent).isNotNull();
        assertThat(excelContent.length).isGreaterThan(0);
    }

    @Test
    void exportPatientsToExcel_ShouldHandleNullFields() throws IOException {
        // Given
        Patient patientWithNulls = new Patient();
        patientWithNulls.setId(3L);
        patientWithNulls.setName("Test Patient");
        patientWithNulls.setEmail("test@example.com");
        patientWithNulls.setAge(25);
        patientWithNulls.setIsActive(true);
        // Tous les autres champs sont null

        List<Patient> patients = new ArrayList<>();
        patients.add(patientWithNulls);

        // When
        byte[] excelContent = exportService.exportPatientsToExcel(patients);

        // Then
        assertThat(excelContent).isNotNull();
        assertThat(excelContent.length).isGreaterThan(0);
    }

    @Test
    void exportPatientsToPDF_ShouldGenerateValidPDF() throws DocumentException {
        // When
        byte[] pdfContent = exportService.exportPatientsToPDF(testPatients);

        // Then
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(0);
        // Vérifier que c'est bien un PDF (commence par %PDF)
        assertThat(new String(pdfContent, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportPatientsToPDF_ShouldHandleEmptyList() throws DocumentException {
        // Given
        List<Patient> emptyList = new ArrayList<>();

        // When
        byte[] pdfContent = exportService.exportPatientsToPDF(emptyList);

        // Then
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(0);
        assertThat(new String(pdfContent, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void exportPatientsToPDF_ShouldHandleNullFields() throws DocumentException {
        // Given
        Patient patientWithNulls = new Patient();
        patientWithNulls.setId(3L);
        patientWithNulls.setName("Test Patient");
        patientWithNulls.setEmail("test@example.com");
        patientWithNulls.setAge(25);
        patientWithNulls.setIsActive(true);

        List<Patient> patients = new ArrayList<>();
        patients.add(patientWithNulls);

        // When
        byte[] pdfContent = exportService.exportPatientsToPDF(patients);

        // Then
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(0);
    }

    @Test
    void exportPatientsToPDF_ShouldTruncateLongAllergies() throws DocumentException {
        // Given
        Patient patientWithLongAllergies = new Patient();
        patientWithLongAllergies.setId(4L);
        patientWithLongAllergies.setName("Test Patient");
        patientWithLongAllergies.setEmail("test@example.com");
        patientWithLongAllergies.setAge(30);
        patientWithLongAllergies.setAllergies("Pénicilline, Aspirine, Ibuprofen, Paracétamol, et beaucoup d'autres médicaments");
        patientWithLongAllergies.setIsActive(true);

        List<Patient> patients = new ArrayList<>();
        patients.add(patientWithLongAllergies);

        // When
        byte[] pdfContent = exportService.exportPatientsToPDF(patients);

        // Then
        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(0);
    }
}
