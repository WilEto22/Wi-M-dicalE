package com.example.crudApp.repository;

import com.example.crudApp.model.DoctorAvailabilityException;
import com.example.crudApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorAvailabilityExceptionRepository extends JpaRepository<DoctorAvailabilityException, Long> {

    /**
     * Trouve une exception par médecin et date
     */
    Optional<DoctorAvailabilityException> findByDoctorAndExceptionDateAndIsActive(
            User doctor, LocalDate exceptionDate, Boolean isActive);

    /**
     * Trouve toutes les exceptions d'un médecin
     */
    List<DoctorAvailabilityException> findByDoctorAndIsActiveOrderByExceptionDateAsc(
            User doctor, Boolean isActive);

    /**
     * Trouve toutes les exceptions d'un médecin pour une plage de dates
     */
    List<DoctorAvailabilityException> findByDoctorAndExceptionDateBetweenAndIsActive(
            User doctor, LocalDate startDate, LocalDate endDate, Boolean isActive);

    /**
     * Trouve toutes les exceptions pour une date donnée
     */
    List<DoctorAvailabilityException> findByExceptionDateAndIsActive(LocalDate exceptionDate, Boolean isActive);

    /**
     * Supprime une exception par médecin et date
     */
    void deleteByDoctorAndExceptionDate(User doctor, LocalDate exceptionDate);
}
