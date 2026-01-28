package com.example.crudApp.specification;

import com.example.crudApp.dto.PatientSearchCriteria;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.Patient;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PatientSpecification
 */
@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class PatientSpecificationTest {

    @Mock
    private Root<Patient> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private jakarta.persistence.criteria.Path<Object> path;

    @Mock
    private jakarta.persistence.criteria.Expression<String> lowerExpression;

    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp() {
        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(lowerExpression);
        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.equal(any(), any())).thenReturn(predicate);
        when(criteriaBuilder.and(any(Predicate[].class))).thenReturn(predicate);
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithNameFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName("Jean");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%jean%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithEmailFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setEmail("test@example.com");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%test@example.com%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithMinAge() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setMinAge(18);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(root, atLeastOnce()).get("age");
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithMaxAge() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setMaxAge(65);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(root, atLeastOnce()).get("age");
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithAddressFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setAddress("Paris");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%paris%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithBloodTypeFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setBloodType("A+");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).equal(any(), eq("A+"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithAllergyFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setAllergy("Pénicilline");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%pénicilline%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithLastVisitAfter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        LocalDate date = LocalDate.of(2024, 1, 1);
        criteria.setLastVisitAfter(date);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(root, atLeastOnce()).get("lastVisit");
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithLastVisitBefore() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        LocalDate date = LocalDate.of(2024, 12, 31);
        criteria.setLastVisitBefore(date);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(root, atLeastOnce()).get("lastVisit");
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithIsActiveFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setIsActive(true);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).equal(any(), eq(true));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithInsuranceNumberFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setInsuranceNumber("123456");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%123456%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithPhoneNumberFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setPhoneNumber("0612345678");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%0612345678%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithEmergencyContactFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setEmergencyContact("Marie Dupont");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).like(any(), eq("%marie dupont%"));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithDoctorSpecialtyFilter() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setDoctorSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).equal(any(), eq(MedicalSpecialty.CARDIOLOGIE));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithMultipleFilters() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName("Jean");
        criteria.setEmail("jean@example.com");
        criteria.setMinAge(30);
        criteria.setMaxAge(60);
        criteria.setBloodType("A+");
        criteria.setIsActive(true);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).and(any(Predicate[].class));
    }

    @Test
    void withCriteria_ShouldCreateSpecification_WithEmptyCriteria() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        verify(criteriaBuilder, atLeastOnce()).and(any(Predicate[].class));
    }

    @Test
    void withCriteria_ShouldHandleNullValues() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName(null);
        criteria.setEmail(null);
        criteria.setMinAge(null);
        criteria.setMaxAge(null);

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
    }

    @Test
    void withCriteria_ShouldHandleEmptyStrings() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName("");
        criteria.setEmail("");
        criteria.setAddress("");

        // When
        Specification<Patient> spec = PatientSpecification.withCriteria(criteria);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        // Then
        assertThat(spec).isNotNull();
        assertThat(result).isNotNull();
        // Empty strings should not create predicates
        verify(criteriaBuilder, never()).like(any(), eq("%%"));
    }
}
