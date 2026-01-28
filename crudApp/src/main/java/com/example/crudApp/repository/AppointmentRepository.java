package com.example.crudApp.repository;

import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.AppointmentStatus;
import com.example.crudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Trouver tous les rendez-vous d'un médecin
    List<Appointment> findByDoctor(User doctor);

    // Trouver tous les rendez-vous d'un patient
    List<Appointment> findByPatient(User patient);

    // Trouver les rendez-vous d'un médecin par statut
    List<Appointment> findByDoctorAndStatus(User doctor, AppointmentStatus status);

    // Trouver les rendez-vous d'un patient par statut
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);

    // Trouver les rendez-vous d'un médecin dans une plage de dates
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime BETWEEN :start AND :end ORDER BY a.appointmentDateTime")
    List<Appointment> findByDoctorAndDateRange(@Param("doctor") User doctor,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    // Trouver les rendez-vous d'un patient dans une plage de dates
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDateTime BETWEEN :start AND :end ORDER BY a.appointmentDateTime")
    List<Appointment> findByPatientAndDateRange(@Param("patient") User patient,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // Vérifier si un créneau est disponible pour un médecin
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime = :dateTime AND a.status IN ('PENDING', 'CONFIRMED')")
    Long countConflictingAppointments(@Param("doctor") User doctor, @Param("dateTime") LocalDateTime dateTime);

    // Trouver les prochains rendez-vous d'un médecin
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime >= :now AND a.status IN ('PENDING', 'CONFIRMED') ORDER BY a.appointmentDateTime")
    List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctor") User doctor, @Param("now") LocalDateTime now);

    // Trouver les prochains rendez-vous d'un patient
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDateTime >= :now AND a.status IN ('PENDING', 'CONFIRMED') ORDER BY a.appointmentDateTime")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patient") User patient, @Param("now") LocalDateTime now);

    // Trouver les rendez-vous par statut dans une plage de dates (pour les rappels)
    List<Appointment> findByStatusAndAppointmentDateTimeBetween(AppointmentStatus status, LocalDateTime start, LocalDateTime end);

    // Trouver les rendez-vous par statut avant une date (pour le nettoyage et auto-complétion)
    List<Appointment> findByStatusAndAppointmentDateTimeBefore(AppointmentStatus status, LocalDateTime dateTime);

    // Trouver un rendez-vous par ID avec les relations chargées (pour éviter les problèmes de lazy loading)
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor LEFT JOIN FETCH a.patient WHERE a.id = :id")
    java.util.Optional<Appointment> findByIdWithRelations(@Param("id") Long id);
}
