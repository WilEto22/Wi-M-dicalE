package com.example.crudApp.repository;

import com.example.crudApp.model.DoctorAvailability;
import com.example.crudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    // Trouver toutes les disponibilités d'un médecin
    List<DoctorAvailability> findByDoctor(User doctor);

    // Trouver les disponibilités actives d'un médecin
    List<DoctorAvailability> findByDoctorAndIsActiveTrue(User doctor);

    // Trouver les disponibilités d'un médecin pour un jour spécifique
    List<DoctorAvailability> findByDoctorAndDayOfWeekAndIsActiveTrue(User doctor, DayOfWeek dayOfWeek);

    // Supprimer toutes les disponibilités d'un médecin
    void deleteByDoctor(User doctor);
}
