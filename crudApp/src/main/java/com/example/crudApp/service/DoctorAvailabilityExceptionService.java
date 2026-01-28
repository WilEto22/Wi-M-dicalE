package com.example.crudApp.service;

import com.example.crudApp.dto.AvailabilityExceptionRequest;
import com.example.crudApp.dto.AvailabilityExceptionResponse;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailabilityException;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.DoctorAvailabilityExceptionRepository;
import com.example.crudApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityExceptionService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityExceptionService.class);

    private final DoctorAvailabilityExceptionRepository exceptionRepository;
    private final UserRepository userRepository;

    /**
     * Crée une nouvelle exception de disponibilité
     */
    @Transactional
    public AvailabilityExceptionResponse createException(AvailabilityExceptionRequest request, String username) {
        logger.info("Création d'une exception pour le médecin: {} à la date: {}", username, request.getExceptionDate());

        User doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé: " + username));

        // Vérifier si une exception existe déjà pour cette date
        exceptionRepository.findByDoctorAndExceptionDateAndIsActive(doctor, request.getExceptionDate(), true)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Une exception existe déjà pour la date: " + request.getExceptionDate());
                });

        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(request.getExceptionDate())
                .reason(request.getReason())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .createdAt(LocalDate.now())
                .build();

        DoctorAvailabilityException saved = exceptionRepository.save(exception);
        logger.info("Exception créée avec succès pour la date: {}", request.getExceptionDate());

        return AvailabilityExceptionResponse.fromException(saved);
    }

    /**
     * Récupère toutes les exceptions d'un médecin
     */
    public List<AvailabilityExceptionResponse> getDoctorExceptions(String username) {
        logger.info("Récupération des exceptions pour le médecin: {}", username);

        User doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé: " + username));

        List<DoctorAvailabilityException> exceptions = exceptionRepository
                .findByDoctorAndIsActiveOrderByExceptionDateAsc(doctor, true);

        return exceptions.stream()
                .map(AvailabilityExceptionResponse::fromException)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une exception par ID
     */
    public AvailabilityExceptionResponse getExceptionById(Long id, String username) {
        logger.info("Récupération de l'exception ID: {} pour le médecin: {}", id, username);

        DoctorAvailabilityException exception = exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception non trouvée avec l'ID: " + id));

        // Vérifier que l'exception appartient au médecin
        if (!exception.getDoctor().getUsername().equals(username)) {
            throw new IllegalArgumentException("Vous n'avez pas accès à cette exception");
        }

        return AvailabilityExceptionResponse.fromException(exception);
    }

    /**
     * Met à jour une exception
     */
    @Transactional
    public AvailabilityExceptionResponse updateException(Long id, AvailabilityExceptionRequest request, String username) {
        logger.info("Mise à jour de l'exception ID: {} par le médecin: {}", id, username);

        DoctorAvailabilityException exception = exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception non trouvée avec l'ID: " + id));

        // Vérifier que l'exception appartient au médecin
        if (!exception.getDoctor().getUsername().equals(username)) {
            throw new IllegalArgumentException("Vous n'avez pas accès à cette exception");
        }

        exception.setExceptionDate(request.getExceptionDate());
        exception.setReason(request.getReason());
        exception.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false);
        exception.setStartTime(request.getStartTime());
        exception.setEndTime(request.getEndTime());

        DoctorAvailabilityException updated = exceptionRepository.save(exception);
        logger.info("Exception mise à jour avec succès");

        return AvailabilityExceptionResponse.fromException(updated);
    }

    /**
     * Supprime une exception (désactive)
     */
    @Transactional
    public void deleteException(Long id, String username) {
        logger.info("Suppression de l'exception ID: {} par le médecin: {}", id, username);

        DoctorAvailabilityException exception = exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exception non trouvée avec l'ID: " + id));

        // Vérifier que l'exception appartient au médecin
        if (!exception.getDoctor().getUsername().equals(username)) {
            throw new IllegalArgumentException("Vous n'avez pas accès à cette exception");
        }

        exception.setIsActive(false);
        exceptionRepository.save(exception);
        logger.info("Exception désactivée avec succès");
    }

    /**
     * Vérifie si une date a une exception pour un médecin
     */
    public DoctorAvailabilityException getExceptionForDate(User doctor, LocalDate date) {
        return exceptionRepository.findByDoctorAndExceptionDateAndIsActive(doctor, date, true)
                .orElse(null);
    }

    /**
     * Récupère toutes les exceptions pour une plage de dates
     */
    public List<DoctorAvailabilityException> getExceptionsForDateRange(User doctor, LocalDate startDate, LocalDate endDate) {
        return exceptionRepository.findByDoctorAndExceptionDateBetweenAndIsActive(doctor, startDate, endDate, true);
    }
}
