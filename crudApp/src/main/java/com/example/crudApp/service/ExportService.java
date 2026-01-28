package com.example.crudApp.service;

import com.example.crudApp.model.Patient;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * Service pour exporter les données en différents formats
 */
@Service
public class ExportService {

    // ==================== MÉTHODES POUR PATIENTS ====================

    /**
     * Exporte les patients en format CSV
     */
    public String exportPatientsToCSV(List<Patient> patients) throws IOException {
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("ID", "Nom", "Email", "Âge", "Adresse", "Groupe Sanguin",
                           "Allergies", "Téléphone", "Contact Urgence", "Numéro Assurance",
                           "Dernière Visite", "Statut"));

        for (Patient patient : patients) {
            csvPrinter.printRecord(
                    patient.getId(),
                    patient.getName(),
                    patient.getEmail(),
                    patient.getAge(),
                    patient.getAddress(),
                    patient.getBloodType(),
                    patient.getAllergies(),
                    patient.getPhoneNumber(),
                    patient.getEmergencyContact(),
                    patient.getInsuranceNumber(),
                    patient.getLastVisit(),
                    patient.getIsActive() ? "Actif" : "Archivé"
            );
        }

        csvPrinter.flush();
        return writer.toString();
    }

    /**
     * Exporte les patients en format Excel
     */
    public byte[] exportPatientsToExcel(List<Patient> patients) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Patients");

        // Style pour l'en-tête
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Créer l'en-tête
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Nom", "Email", "Âge", "Adresse", "Groupe Sanguin",
                           "Allergies", "Téléphone", "Contact Urgence", "Tél. Urgence",
                           "Numéro Assurance", "Dernière Visite", "Statut"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Remplir les données
        int rowNum = 1;
        for (Patient patient : patients) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(patient.getId());
            row.createCell(1).setCellValue(patient.getName());
            row.createCell(2).setCellValue(patient.getEmail());
            row.createCell(3).setCellValue(patient.getAge());
            row.createCell(4).setCellValue(patient.getAddress());
            row.createCell(5).setCellValue(patient.getBloodType() != null ? patient.getBloodType() : "");
            row.createCell(6).setCellValue(patient.getAllergies() != null ? patient.getAllergies() : "");
            row.createCell(7).setCellValue(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "");
            row.createCell(8).setCellValue(patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "");
            row.createCell(9).setCellValue(patient.getEmergencyPhone() != null ? patient.getEmergencyPhone() : "");
            row.createCell(10).setCellValue(patient.getInsuranceNumber() != null ? patient.getInsuranceNumber() : "");
            row.createCell(11).setCellValue(patient.getLastVisit() != null ? patient.getLastVisit().toString() : "");
            row.createCell(12).setCellValue(patient.getIsActive() ? "Actif" : "Archivé");
        }

        // Auto-size des colonnes
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Écrire dans un ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Exporte les patients en format PDF
     */
    public byte[] exportPatientsToPDF(List<Patient> patients) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate()); // Paysage pour plus de colonnes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Titre
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Liste des Patients", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Créer le tableau (8 colonnes principales)
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // En-têtes
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"ID", "Nom", "Email", "Âge", "Groupe Sanguin", "Allergies", "Téléphone", "Statut"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(41, 128, 185)); // Bleu médical
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Données
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (Patient patient : patients) {
            table.addCell(new Phrase(String.valueOf(patient.getId()), dataFont));
            table.addCell(new Phrase(patient.getName(), dataFont));
            table.addCell(new Phrase(patient.getEmail(), dataFont));
            table.addCell(new Phrase(String.valueOf(patient.getAge()), dataFont));
            table.addCell(new Phrase(patient.getBloodType() != null ? patient.getBloodType() : "-", dataFont));

            // Tronquer les allergies si trop longues
            String allergies = patient.getAllergies() != null ? patient.getAllergies() : "-";
            if (allergies.length() > 30) {
                allergies = allergies.substring(0, 27) + "...";
            }
            table.addCell(new Phrase(allergies, dataFont));

            table.addCell(new Phrase(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "-", dataFont));
            table.addCell(new Phrase(patient.getIsActive() ? "Actif" : "Archivé", dataFont));
        }

        document.add(table);

        // Pied de page
        Paragraph footer = new Paragraph("Document confidentiel - Généré le : " + new java.util.Date(),
                FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY));
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();

        return outputStream.toByteArray();
    }
}
