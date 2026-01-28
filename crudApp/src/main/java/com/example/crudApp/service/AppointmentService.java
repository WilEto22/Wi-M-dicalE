package com.example.crudApp.service;

import com.example.crudApp.dto.AppointmentRequest;
import com.example.crudApp.dto.AvailableSlotResponse;
import com.example.crudApp.exception.AppointmentModificationNotAllowedException;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.exception.ServiceException;
import com.example.crudApp.model.*;
import com.example.crudApp.repository.AppointmentRepository;
import com.example.crudApp.repository.DoctorAvailabilityRepository;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.util.BusinessDayCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final DoctorAvailabilityExceptionService exceptionService;

    /**
     * Créer un nouveau rendez-vous
     */
    @Transactional
    public Appointment createAppointment(AppointmentRequest request, String patientUsername) {
        logger.info("Création d'un rendez-vous pour le patient: {} avec le médecin ID: {}",
                patientUsername, request.getDoctorId());

        // Valider que la date et l'heure du rendez-vous sont fournies
        LocalDateTime appointmentDateTime = request.getAppointmentDateTime();
        if (appointmentDateTime == null) {
            throw new IllegalArgumentException("La date et l'heure du rendez-vous sont obligatoires");
        }

        // Récupérer le patient
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));

        if (patient.getUserType() != UserType.PATIENT) {
            throw new IllegalArgumentException("Seuls les patients peuvent prendre des rendez-vous");
        }

        // Récupérer le médecin
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID: " + request.getDoctorId()));

        if (doctor.getUserType() != UserType.DOCTOR) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un médecin");
        }

        // Vérifier que le créneau est disponible
        if (!isSlotAvailable(doctor, appointmentDateTime)) {
            throw new IllegalArgumentException("Ce créneau n'est pas disponible");
        }

        // Créer le rendez-vous
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(appointmentDateTime)
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        logger.info("Rendez-vous créé avec succès - ID: {}", saved.getId());

        // Envoyer les emails de notification
        try {
            emailService.sendAppointmentConfirmationEmail(saved);
            emailService.sendAppointmentNotificationToDoctor(saved);
            logger.info("Emails de notification envoyés pour le rendez-vous ID: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi des emails de notification: {}", e.getMessage());
            // Ne pas bloquer la création du rendez-vous si l'email échoue
        }

        return saved;
    }

    /**
     * Vérifier si un créneau est disponible
     */
    public boolean isSlotAvailable(User doctor, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();

        // Vérifier s'il y a une exception pour cette date
        DoctorAvailabilityException exception = exceptionService.getExceptionForDate(doctor, date);
        if (exception != null) {
            // Si l'exception indique que le médecin n'est pas disponible
            if (!exception.getIsAvailable()) {
                return false;
            }
            // Si l'exception a des horaires spécifiques, vérifier que le créneau est dans ces horaires
            if (exception.getStartTime() != null && exception.getEndTime() != null) {
                LocalTime time = dateTime.toLocalTime();
                LocalTime exceptionStart = LocalTime.parse(exception.getStartTime());
                LocalTime exceptionEnd = LocalTime.parse(exception.getEndTime());
                if (time.isBefore(exceptionStart) || !time.isBefore(exceptionEnd)) {
                    return false;
                }
            }
        }

        // Vérifier qu'il n'y a pas déjà un rendez-vous à ce créneau
        Long conflicts = appointmentRepository.countConflictingAppointments(doctor, dateTime);
        if (conflicts > 0) {
            return false;
        }

        // Vérifier que le créneau correspond aux disponibilités du médecin
        List<DoctorAvailability> availabilities = availabilityRepository
                .findByDoctorAndDayOfWeekAndIsActiveTrue(doctor, dateTime.getDayOfWeek());

        if (availabilities.isEmpty()) {
            return false;
        }

        LocalTime time = dateTime.toLocalTime();
        for (DoctorAvailability availability : availabilities) {
            if (!time.isBefore(availability.getStartTime()) && time.isBefore(availability.getEndTime())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Obtenir les créneaux disponibles pour un médecin à une date donnée
     */
    public List<AvailableSlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID: " + doctorId));

        if (doctor.getUserType() != UserType.DOCTOR) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un médecin");
        }

        // Vérifier s'il y a une exception pour cette date
        DoctorAvailabilityException exception = exceptionService.getExceptionForDate(doctor, date);

        // Si l'exception indique que le médecin n'est pas disponible, retourner une liste vide
        if (exception != null && !exception.getIsAvailable()) {
            logger.info("Le médecin n'est pas disponible le {} en raison d'une exception: {}",
                    date, exception.getReason());
            return new ArrayList<>();
        }

        List<DoctorAvailability> availabilities = availabilityRepository
                .findByDoctorAndDayOfWeekAndIsActiveTrue(doctor, date.getDayOfWeek());

        List<AvailableSlotResponse> slots = new ArrayList<>();

        for (DoctorAvailability availability : availabilities) {
            LocalTime currentTime = availability.getStartTime();
            LocalTime endTime = availability.getEndTime();

            // Si l'exception a des horaires spécifiques, utiliser ceux-ci
            if (exception != null && exception.getStartTime() != null && exception.getEndTime() != null) {
                currentTime = LocalTime.parse(exception.getStartTime());
                endTime = LocalTime.parse(exception.getEndTime());
            }

            while (currentTime.isBefore(endTime)) {
                LocalDateTime slotDateTime = LocalDateTime.of(date, currentTime);
                boolean available = isSlotAvailable(doctor, slotDateTime) && slotDateTime.isAfter(LocalDateTime.now());
                slots.add(AvailableSlotResponse.of(slotDateTime, available));
                currentTime = currentTime.plusMinutes(availability.getSlotDurationMinutes());
            }
        }

        return slots;
    }

    /**
     * Obtenir tous les rendez-vous d'un patient
     */
    public List<Appointment> getPatientAppointments(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        return appointmentRepository.findByPatient(patient);
    }

    /**
     * Obtenir tous les rendez-vous d'un médecin
     */
    public List<Appointment> getDoctorAppointments(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        return appointmentRepository.findByDoctor(doctor);
    }

    /**
     * Obtenir les prochains rendez-vous d'un patient
     */
    public List<Appointment> getUpcomingPatientAppointments(String patientUsername) {
        User patient = userRepository.findByUsername(patientUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé"));
        return appointmentRepository.findUpcomingAppointmentsByPatient(patient, LocalDateTime.now());
    }

    /**
     * Obtenir les prochains rendez-vous d'un médecin
     */
    public List<Appointment> getUpcomingDoctorAppointments(String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
        return appointmentRepository.findUpcomingAppointmentsByDoctor(doctor, LocalDateTime.now());
    }

    /**
     * Confirmer un rendez-vous (par le médecin)
     */
    @Transactional
    public Appointment confirmAppointment(Long appointmentId, String doctorUsername) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'ID: " + appointmentId));

        if (!appointment.getDoctor().getUsername().equals(doctorUsername)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à confirmer ce rendez-vous");
        }

        String oldStatus = appointment.getStatus().toString();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment updated = appointmentRepository.save(appointment);

        // Envoyer un email de confirmation au patient
        try {
            emailService.sendAppointmentStatusUpdateEmail(updated, oldStatus);
            logger.info("Email de confirmation envoyé au patient pour le rendez-vous ID: {}", updated.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation: {}", e.getMessage());
        }

        return updated;
    }

    /**
     * Annuler un rendez-vous
     */
    @Transactional
    public Appointment cancelAppointment(Long appointmentId, String username) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'ID: " + appointmentId));

        // Vérifier que l'utilisateur est soit le patient soit le médecin
        if (!appointment.getDoctor().getUsername().equals(username) &&
            !appointment.getPatient().getUsername().equals(username)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à annuler ce rendez-vous");
        }

        // Si c'est un patient qui annule, vérifier la règle des 24h
        if (appointment.getPatient().getUsername().equals(username)) {
            validateModificationDeadline(appointment);
        }

        String oldStatus = appointment.getStatus().toString();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);

        // Envoyer un email de notification d'annulation
        try {
            emailService.sendAppointmentStatusUpdateEmail(updated, oldStatus);
            logger.info("Email d'annulation envoyé pour le rendez-vous ID: {}", updated.getId());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email d'annulation: {}", e.getMessage());
        }

        return updated;
    }

    /**
     * Marquer un rendez-vous comme terminé et ajouter des notes
     */
    @Transactional
    public Appointment completeAppointment(Long appointmentId, String doctorUsername, String notes) {
        logger.info("Tentative de terminer le rendez-vous ID: {} par le médecin: {}", appointmentId, doctorUsername);

        // Récupérer le rendez-vous avec les relations chargées
        Appointment appointment = appointmentRepository.findByIdWithRelations(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'ID: " + appointmentId));

        // Vérifier que le médecin est autorisé
        if (!appointment.getDoctor().getUsername().equals(doctorUsername)) {
            throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce rendez-vous");
        }

        String oldStatus = appointment.getStatus().toString();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setDoctorNotes(notes);

        try {
            Appointment updated = appointmentRepository.save(appointment);
            logger.info("Rendez-vous ID: {} marqué comme terminé avec succès", updated.getId());

            // Envoyer un email de notification au patient
            try {
                emailService.sendAppointmentStatusUpdateEmail(updated, oldStatus);
                logger.info("Email de fin de consultation envoyé au patient pour le rendez-vous ID: {}", updated.getId());
            } catch (Exception e) {
                logger.error("Erreur lors de l'envoi de l'email de fin de consultation: {}", e.getMessage());
            }

            return updated;
        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde du rendez-vous: {}", e.getMessage(), e);
            throw new ServiceException("Erreur lors de la mise à jour du rendez-vous: " + e.getMessage(), e);
        }
    }

    /**
     * Obtenir un rendez-vous par ID
     */
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));
    }

    /**
     * Valider que la modification/annulation respecte le délai de 24h (jours ouvrables)
     * Pour les rendez-vous CONFIRMÉS, on exige 1 jour ouvrable complet (24h ouvrables)
     * Pour les rendez-vous PENDING, on exige simplement 24h
     */
    private void validateModificationDeadline(Appointment appointment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();

        // Pour les rendez-vous confirmés, vérifier les jours ouvrables
        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            long businessDays = BusinessDayCalculator.countBusinessDaysBetween(now, appointmentTime);

            if (businessDays < 1) {
                throw new AppointmentModificationNotAllowedException(
                    "Les rendez-vous confirmés ne peuvent être annulés qu'avec au moins 1 jour ouvrable d'avance. " +
                    "Il ne reste que " + businessDays + " jour(s) ouvrable(s) avant votre rendez-vous."
                );
            }
        } else {
            // Pour les rendez-vous en attente, vérifier simplement 24h
            Duration duration = Duration.between(now, appointmentTime);
            long hoursUntilAppointment = duration.toHours();

            if (hoursUntilAppointment < 24) {
                throw new AppointmentModificationNotAllowedException(
                    "Les rendez-vous ne peuvent être annulés que 24 heures à l'avance. " +
                    "Il ne reste que " + hoursUntilAppointment + " heures avant votre rendez-vous."
                );
            }
        }
    }
}
