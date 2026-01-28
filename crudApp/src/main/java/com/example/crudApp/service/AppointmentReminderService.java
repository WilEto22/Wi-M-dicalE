package com.example.crudApp.service;

import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.AppointmentStatus;
import com.example.crudApp.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de planification pour l'envoi automatique de rappels de rendez-vous
 */
@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentReminderService.class);

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    /**
     * Tâche planifiée pour envoyer des rappels 24h avant les rendez-vous
     * S'exécute toutes les heures
     */
    @Scheduled(cron = "0 0 * * * *") // Toutes les heures à la minute 0
    @Transactional(readOnly = true)
    public void sendAppointmentReminders() {
        logger.info("Démarrage de la tâche d'envoi de rappels de rendez-vous");

        try {
            // Calculer la fenêtre de temps : entre 23h et 25h dans le futur
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderWindowStart = now.plusHours(23);
            LocalDateTime reminderWindowEnd = now.plusHours(25);

            // Récupérer les rendez-vous confirmés dans cette fenêtre
            List<Appointment> upcomingAppointments = appointmentRepository
                    .findByStatusAndAppointmentDateTimeBetween(
                            AppointmentStatus.CONFIRMED,
                            reminderWindowStart,
                            reminderWindowEnd
                    );

            logger.info("Trouvé {} rendez-vous nécessitant un rappel", upcomingAppointments.size());

            // Envoyer un rappel pour chaque rendez-vous
            int successCount = 0;
            int failureCount = 0;

            for (Appointment appointment : upcomingAppointments) {
                try {
                    emailService.sendAppointmentReminderEmail(appointment);
                    successCount++;
                    logger.debug("Rappel envoyé pour le rendez-vous ID: {}", appointment.getId());
                } catch (Exception e) {
                    failureCount++;
                    logger.error("Erreur lors de l'envoi du rappel pour le rendez-vous ID: {} - {}",
                            appointment.getId(), e.getMessage());
                }
            }

            logger.info("Tâche de rappels terminée - Succès: {}, Échecs: {}", successCount, failureCount);

        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution de la tâche de rappels: {}", e.getMessage(), e);
        }
    }

    /**
     * Tâche planifiée pour nettoyer les anciens rendez-vous
     * S'exécute tous les jours à 2h du matin
     */
    @Scheduled(cron = "0 0 2 * * *") // Tous les jours à 2h00
    @Transactional
    public void cleanupOldAppointments() {
        logger.info("Démarrage de la tâche de nettoyage des anciens rendez-vous");

        try {
            // Supprimer les rendez-vous annulés de plus de 6 mois
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

            List<Appointment> oldCancelledAppointments = appointmentRepository
                    .findByStatusAndAppointmentDateTimeBefore(AppointmentStatus.CANCELLED, sixMonthsAgo);

            if (!oldCancelledAppointments.isEmpty()) {
                appointmentRepository.deleteAll(oldCancelledAppointments);
                logger.info("Supprimé {} rendez-vous annulés de plus de 6 mois", oldCancelledAppointments.size());
            } else {
                logger.info("Aucun ancien rendez-vous à nettoyer");
            }

        } catch (Exception e) {
            logger.error("Erreur lors du nettoyage des anciens rendez-vous: {}", e.getMessage(), e);
        }
    }

    /**
     * Tâche planifiée pour marquer automatiquement les rendez-vous passés comme terminés
     * S'exécute toutes les 6 heures
     */
    @Scheduled(cron = "0 0 */6 * * *") // Toutes les 6 heures
    @Transactional
    public void autoCompletePassedAppointments() {
        logger.info("Démarrage de la tâche de complétion automatique des rendez-vous passés");

        try {
            LocalDateTime now = LocalDateTime.now();

            // Trouver les rendez-vous confirmés qui sont passés
            List<Appointment> passedAppointments = appointmentRepository
                    .findByStatusAndAppointmentDateTimeBefore(AppointmentStatus.CONFIRMED, now);

            int completedCount = 0;

            for (Appointment appointment : passedAppointments) {
                // Marquer comme terminé avec une note automatique
                appointment.setStatus(AppointmentStatus.COMPLETED);
                appointment.setDoctorNotes("Rendez-vous marqué automatiquement comme terminé");
                appointmentRepository.save(appointment);
                completedCount++;

                logger.debug("Rendez-vous ID: {} marqué automatiquement comme terminé", appointment.getId());
            }

            if (completedCount > 0) {
                logger.info("Marqué {} rendez-vous comme terminés automatiquement", completedCount);
            } else {
                logger.info("Aucun rendez-vous passé à marquer comme terminé");
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la complétion automatique des rendez-vous: {}", e.getMessage(), e);
        }
    }

    /**
     * Méthode manuelle pour envoyer un rappel immédiat (pour tests)
     */
    public void sendImmediateReminder(Long appointmentId) {
        logger.info("Envoi d'un rappel immédiat pour le rendez-vous ID: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé"));

        emailService.sendAppointmentReminderEmail(appointment);
        logger.info("Rappel immédiat envoyé pour le rendez-vous ID: {}", appointmentId);
    }
}
