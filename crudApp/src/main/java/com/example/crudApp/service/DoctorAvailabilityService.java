package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailability;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.repository.DoctorAvailabilityRepository;
import com.example.crudApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorAvailabilityService.class);

    private final DoctorAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    /**
     * Créer une nouvelle disponibilité pour un médecin
     */
    @Transactional
    public DoctorAvailability createAvailability(DoctorAvailability availability, String doctorUsername) {
        logger.info("Création d'une disponibilité pour le médecin: {}", doctorUsername);

        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));

        if (doctor.getUserType() != UserType.DOCTOR) {
            throw new IllegalArgumentException("Seuls les médecins peuvent définir des disponibilités");
        }

        availability.setDoctor(doctor);
        return availabilityRepository.save(availability);
    }

    /**
     * Obtenir toutes les disponibilités d'un médecin
     */
    public List<DoctorAvailability> getDoctorAvailabilities(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        return availabilityRepository.findByDoctor(doctor);
    }

    /**
     * Obtenir les disponibilités actives d'un médecin
     */
    public List<DoctorAvailability> getActiveDoctorAvailabilities(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID: " + doctorId));
        return availabilityRepository.findByDoctorAndIsActiveTrue(doctor);
    }

    /**
     * Mettre à jour une disponibilité
     */
    @Transactional
    public DoctorAvailability updateAvailability(Long id, DoctorAvailability updatedAvailability, String doctorUsername) {
        DoctorAvailability existing = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id));

        if (!existing.getDoctor().getUsername().equals(doctorUsername)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier cette disponibilité");
        }

        existing.setDayOfWeek(updatedAvailability.getDayOfWeek());
        existing.setStartTime(updatedAvailability.getStartTime());
        existing.setEndTime(updatedAvailability.getEndTime());
        existing.setSlotDurationMinutes(updatedAvailability.getSlotDurationMinutes());
        existing.setIsActive(updatedAvailability.getIsActive());

        return availabilityRepository.save(existing);
    }

    /**
     * Supprimer une disponibilité
     */
    @Transactional
    public void deleteAvailability(Long id, String doctorUsername) {
        DoctorAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'ID: " + id));

        if (!availability.getDoctor().getUsername().equals(doctorUsername)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer cette disponibilité");
        }

        availabilityRepository.delete(availability);
    }
}
