package com.example.crudApp.specification;

import com.example.crudApp.dto.PatientSearchCriteria;
import com.example.crudApp.model.Patient;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PatientSpecification {

    public static Specification<Patient> withCriteria(PatientSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Recherche par nom (case insensitive)
            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.getName().toLowerCase() + "%"
                ));
            }

            // Recherche par email (case insensitive)
            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase() + "%"
                ));
            }

            // Recherche par âge minimum
            if (criteria.getMinAge() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("age"),
                        criteria.getMinAge()
                ));
            }

            // Recherche par âge maximum
            if (criteria.getMaxAge() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("age"),
                        criteria.getMaxAge()
                ));
            }

            // Recherche par adresse (case insensitive)
            if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address")),
                        "%" + criteria.getAddress().toLowerCase() + "%"
                ));
            }

            // Recherche par groupe sanguin
            if (criteria.getBloodType() != null && !criteria.getBloodType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("bloodType"),
                        criteria.getBloodType()
                ));
            }

            // Recherche par allergie (case insensitive)
            if (criteria.getAllergy() != null && !criteria.getAllergy().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("allergies")),
                        "%" + criteria.getAllergy().toLowerCase() + "%"
                ));
            }

            // Recherche par date de dernière visite (après)
            if (criteria.getLastVisitAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("lastVisit"),
                        criteria.getLastVisitAfter()
                ));
            }

            // Recherche par date de dernière visite (avant)
            if (criteria.getLastVisitBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("lastVisit"),
                        criteria.getLastVisitBefore()
                ));
            }

            // Recherche par statut actif
            if (criteria.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("isActive"),
                        criteria.getIsActive()
                ));
            }

            // Recherche par numéro d'assurance
            if (criteria.getInsuranceNumber() != null && !criteria.getInsuranceNumber().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("insuranceNumber")),
                        "%" + criteria.getInsuranceNumber().toLowerCase() + "%"
                ));
            }

            // Recherche par numéro de téléphone
            if (criteria.getPhoneNumber() != null && !criteria.getPhoneNumber().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("phoneNumber"),
                        "%" + criteria.getPhoneNumber() + "%"
                ));
            }

            // Recherche par contact d'urgence
            if (criteria.getEmergencyContact() != null && !criteria.getEmergencyContact().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("emergencyContact")),
                        "%" + criteria.getEmergencyContact().toLowerCase() + "%"
                ));
            }

            // Recherche par spécialité du médecin
            if (criteria.getDoctorSpecialty() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("user").get("specialty"),
                        criteria.getDoctorSpecialty()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
