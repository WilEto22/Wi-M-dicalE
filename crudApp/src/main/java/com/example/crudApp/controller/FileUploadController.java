package com.example.crudApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Upload", description = "API pour l'upload de fichiers")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${file.upload-dir:uploads/profile-photos}")
    private String uploadDir;

    @PostMapping("/upload-profile-photo")
    @Operation(summary = "Upload d'une photo de profil")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            // Vérifier si le fichier est vide
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier est vide"));
            }

            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }

            // Vérifier la taille du fichier (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "La taille du fichier ne doit pas dépasser 5MB"));
            }

            // Créer le répertoire s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construire l'URL du fichier (URL complète avec le serveur backend)
            String fileUrl = "http://localhost:8080/uploads/profile-photos/" + filename;

            logger.info("Photo de profil uploadée avec succès: {} - URL: {}", filename, fileUrl);

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("filename", filename);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Erreur lors de l'upload du fichier", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'upload du fichier: " + e.getMessage()));
        }
    }
}
