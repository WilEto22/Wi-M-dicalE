package com.example.crudApp.service;

import com.example.crudApp.model.UserType;
import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    private final String appName = "Wi M-dical"; // Nom de l'application fixe

    @Async
    public void sendWelcomeEmail(User user) {
        try {
            String userEmail = user.getEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                log.warn("Impossible d'envoyer l'email de bienvenue : l'utilisateur {} n'a pas d'adresse email", user.getId());
                return;
            }

            String subject = "Bienvenue sur " + appName;
            String body = buildWelcomeEmailBody(user);

            sendHtmlEmail(userEmail, subject, body);
            log.info("Email de bienvenue envoyé à l'utilisateur {} ({})", user.getId(), userEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue pour l'utilisateur {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    @Async
    public void sendAppointmentConfirmationEmail(Appointment appointment) {
        try {
            User patient = appointment.getPatient();
            User doctor = appointment.getDoctor();

            String patientEmail = patient.getEmail();
            if (patientEmail == null || patientEmail.isEmpty()) {
                log.warn("Impossible d'envoyer l'email de confirmation : le patient {} n'a pas d'adresse email", patient.getId());
                return;
            }

            String subject = "Confirmation de votre demande de rendez-vous";
            String body = buildAppointmentConfirmationEmailBody(appointment, patient, doctor);

            sendHtmlEmail(patientEmail, subject, body);
            log.info("Email de confirmation envoyé au patient {} pour le rendez-vous {}", patient.getId(), appointment.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de confirmation pour le rendez-vous {}: {}", appointment.getId(), e.getMessage(), e);
        }
    }

    @Async
    public void sendAppointmentNotificationToDoctor(Appointment appointment) {
        try {
            User doctor = appointment.getDoctor();
            User patient = appointment.getPatient();

            String doctorEmail = doctor.getEmail();
            if (doctorEmail == null || doctorEmail.isEmpty()) {
                log.warn("Impossible d'envoyer l'email au médecin {} : pas d'adresse email", doctor.getId());
                return;
            }

            String subject = "Nouvelle demande de rendez-vous";
            String body = buildDoctorNotificationEmailBody(appointment, patient, doctor);

            sendHtmlEmail(doctorEmail, subject, body);
            log.info("Email de notification envoyé au médecin {} pour le rendez-vous {}", doctor.getId(), appointment.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email au médecin pour le rendez-vous {}: {}", appointment.getId(), e.getMessage(), e);
        }
    }

    @Async
    public void sendAppointmentStatusUpdateEmail(Appointment appointment, String oldStatus) {
        try {
            User patient = appointment.getPatient();
            String patientEmail = patient.getEmail();
            if (patientEmail == null || patientEmail.isEmpty()) {
                log.warn("Impossible d'envoyer l'email de mise à jour : le patient {} n'a pas d'adresse email", patient.getId());
                return;
            }

            String subject = "Mise à jour de votre rendez-vous";
            String body = buildStatusUpdateEmailBody(appointment, oldStatus);

            sendHtmlEmail(patientEmail, subject, body);
            log.info("Email de mise à jour envoyé au patient {} pour le rendez-vous {}", patient.getId(), appointment.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de mise à jour pour le rendez-vous {}: {}", appointment.getId(), e.getMessage(), e);
        }
    }

    @Async
    public void sendAppointmentReminderEmail(Appointment appointment) {
        try {
            User patient = appointment.getPatient();
            String patientEmail = patient.getEmail();
            if (patientEmail == null || patientEmail.isEmpty()) {
                log.warn("Impossible d'envoyer l'email de rappel : le patient {} n'a pas d'adresse email", patient.getId());
                return;
            }

            String subject = "Rappel : Rendez-vous médical demain";
            String body = buildReminderEmailBody(appointment);

            sendHtmlEmail(patientEmail, subject, body);
            log.info("Email de rappel envoyé au patient {} pour le rendez-vous {}", patient.getId(), appointment.getId());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de rappel pour le rendez-vous {}: {}", appointment.getId(), e.getMessage(), e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@wimedical.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private String buildWelcomeEmailBody(User user) {

        String appName = "Wi-M-dicalE";

        UserType userType = user.getUserType() != null
                ? user.getUserType()
                : UserType.PATIENT;

        boolean isDoctor = userType == UserType.DOCTOR;

        String greeting = isDoctor ? "Dr. " : "";
        String fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();

        String accountTypeLabel = userType.getDisplayName();

        String specificMessage = isDoctor
                ? "En tant que médecin, vous pouvez maintenant gérer vos rendez-vous, consulter votre agenda et recevoir des demandes de rendez-vous."
                : "En tant que patient, vous pouvez maintenant rechercher des médecins et prendre rendez-vous en ligne.";

        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; }
                .info-box { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }
                .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🎉 Bienvenue sur %s !</h1>
                </div>
                <div class="content">
                    <h2>Bonjour %s%s,</h2>
                    <p>Nous sommes ravis de vous accueillir sur notre plateforme de gestion de rendez-vous médicaux !</p>

                    <div class="info-box">
                        <h3>✅ Votre compte a été créé avec succès</h3>
                        <p><strong>Nom d'utilisateur :</strong> %s</p>
                        <p><strong>Email :</strong> %s</p>
                        <p><strong>Type de compte :</strong> %s</p>
                    </div>

                    <div class="info-box">
                        <h3>🚀 Prochaines étapes</h3>
                        <p>%s</p>
                        <ul>
                            <li>Connectez-vous à votre espace personnel</li>
                            <li>Complétez votre profil</li>
                            <li>Explorez toutes les fonctionnalités disponibles</li>
                        </ul>
                    </div>

                    <p>Si vous avez des questions ou besoin d'aide, n'hésitez pas à nous contacter.</p>
                    <p>Cordialement,<br>L'équipe %s</p>
                </div>
                <div class="footer">
                    <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                    <p>© 2025 %s - Tous droits réservés</p>
                </div>
            </div>
        </body>
        </html>
        """,
                appName,
                greeting,
                fullName,
                user.getUsername(),
                user.getEmail(),
                accountTypeLabel,
                specificMessage,
                appName,
                appName
        );
    }


    private String buildAppointmentConfirmationEmailBody(Appointment appointment, User patient, User doctor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String appointmentDate = appointment.getAppointmentDateTime().format(formatter);
        String patientName = patient.getFullName() != null ? patient.getFullName() : patient.getUsername();
        String doctorName = doctor.getFullName() != null ? doctor.getFullName() : doctor.getUsername();
        return String.format("""
                <h2>Bonjour %s,</h2>
                <p>Votre rendez-vous avec Dr. %s le %s a été enregistré.</p>
                """, patientName, doctorName, appointmentDate);
    }

    private String buildDoctorNotificationEmailBody(Appointment appointment, User patient, User doctor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String appointmentDate = appointment.getAppointmentDateTime().format(formatter);
        String patientName = patient.getFullName() != null ? patient.getFullName() : patient.getUsername();
        String doctorName = doctor.getFullName() != null ? doctor.getFullName() : doctor.getUsername();
        return String.format("""
                <h2>Bonjour Dr. %s,</h2>
                <p>Vous avez un nouveau rendez-vous avec %s le %s.</p>
                """, doctorName, patientName, appointmentDate);
    }

    private String buildStatusUpdateEmailBody(Appointment appointment, String oldStatus) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String appointmentDate = appointment.getAppointmentDateTime().format(formatter);
        User patient = appointment.getPatient();
        String patientName = patient.getFullName() != null ? patient.getFullName() : patient.getUsername();
        return String.format("""
                <h2>Bonjour %s,</h2>
                <p>Le statut de votre rendez-vous du %s a été mis à jour : %s → %s</p>
                """, patientName, appointmentDate, oldStatus, appointment.getStatus());
    }

    private String buildReminderEmailBody(Appointment appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String appointmentDate = appointment.getAppointmentDateTime().format(formatter);
        User patient = appointment.getPatient();
        User doctor = appointment.getDoctor();
        String patientName = patient.getFullName() != null ? patient.getFullName() : patient.getUsername();
        String doctorName = doctor.getFullName() != null ? doctor.getFullName() : doctor.getUsername();
        return String.format("""
                <h2>Bonjour %s,</h2>
                <p>Rappel : vous avez un rendez-vous avec Dr. %s demain (%s)</p>
                """, patientName, doctorName, appointmentDate);
    }
}
