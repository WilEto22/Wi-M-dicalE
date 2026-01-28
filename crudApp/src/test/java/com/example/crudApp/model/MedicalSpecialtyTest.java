package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MedicalSpecialtyTest {

    @Test
    void testCardiologieSpecialty() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.CARDIOLOGIE;

        // Then
        assertThat(specialty).isNotNull();
        assertThat(specialty.getDisplayName()).isEqualTo("Cardiologie");
        assertThat(specialty.getDescription()).isEqualTo("Cœur et vaisseaux sanguins");
        assertThat(specialty.toString()).isEqualTo("Cardiologie");
    }

    @Test
    void testDermatologieSpecialty() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.DERMATOLOGIE;

        // Then
        assertThat(specialty).isNotNull();
        assertThat(specialty.getDisplayName()).isEqualTo("Dermatologie");
        assertThat(specialty.getDescription()).isEqualTo("Peau, cheveux, ongles");
    }

    @Test
    void testMedecineGeneraleSpecialty() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.MEDECINE_GENERALE;

        // Then
        assertThat(specialty).isNotNull();
        assertThat(specialty.getDisplayName()).isEqualTo("Médecine générale");
        assertThat(specialty.getDescription()).contains("Suivi global du patient");
    }

    @Test
    void testFromStringWithEnumName() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.fromString("CARDIOLOGIE");

        // Then
        assertThat(specialty).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testFromStringWithDisplayName() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.fromString("Cardiologie");

        // Then
        assertThat(specialty).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testFromStringWithEnglishAlias() {
        // When
        MedicalSpecialty cardiology = MedicalSpecialty.fromString("CARDIOLOGY");
        MedicalSpecialty dermatology = MedicalSpecialty.fromString("DERMATOLOGY");

        // Then
        assertThat(cardiology).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(dermatology).isEqualTo(MedicalSpecialty.DERMATOLOGIE);
    }

    @Test
    void testFromStringCaseInsensitive() {
        // When
        MedicalSpecialty specialty1 = MedicalSpecialty.fromString("cardiologie");
        MedicalSpecialty specialty2 = MedicalSpecialty.fromString("CARDIOLOGIE");
        MedicalSpecialty specialty3 = MedicalSpecialty.fromString("Cardiologie");

        // Then
        assertThat(specialty1).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(specialty2).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(specialty3).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testFromStringWithNull() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.fromString(null);

        // Then
        assertThat(specialty).isNull();
    }

    @Test
    void testFromStringWithInvalidValue() {
        // When & Then
        assertThatThrownBy(() -> MedicalSpecialty.fromString("INVALID_SPECIALTY"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Spécialité inconnue");
    }

    @Test
    void testToValue() {
        // When
        String value = MedicalSpecialty.CARDIOLOGIE.toValue();

        // Then
        assertThat(value).isEqualTo("CARDIOLOGIE");
    }

    @Test
    void testAllSpecialtiesHaveDisplayNameAndDescription() {
        // When
        MedicalSpecialty[] specialties = MedicalSpecialty.values();

        // Then
        for (MedicalSpecialty specialty : specialties) {
            assertThat(specialty.getDisplayName()).isNotNull().isNotEmpty();
            assertThat(specialty.getDescription()).isNotNull().isNotEmpty();
        }
    }

    @Test
    void testAllValues() {
        // When
        MedicalSpecialty[] specialties = MedicalSpecialty.values();

        // Then
        assertThat(specialties).hasSizeGreaterThan(30);
        assertThat(specialties).contains(
                MedicalSpecialty.MEDECINE_GENERALE,
                MedicalSpecialty.CARDIOLOGIE,
                MedicalSpecialty.DERMATOLOGIE,
                MedicalSpecialty.PEDIATRIE,
                MedicalSpecialty.PSYCHIATRIE
        );
    }

    @Test
    void testValueOf() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.valueOf("CARDIOLOGIE");

        // Then
        assertThat(specialty).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testEnumEquality() {
        // Given
        MedicalSpecialty specialty1 = MedicalSpecialty.CARDIOLOGIE;
        MedicalSpecialty specialty2 = MedicalSpecialty.CARDIOLOGIE;
        MedicalSpecialty specialty3 = MedicalSpecialty.DERMATOLOGIE;

        // Then
        assertThat(specialty1).isEqualTo(specialty2);
        assertThat(specialty1).isNotEqualTo(specialty3);
    }

    @Test
    void testSurgicalSpecialties() {
        // Test some surgical specialties
        assertThat(MedicalSpecialty.CHIRURGIE_GENERALE.getDisplayName()).contains("Chirurgie");
        assertThat(MedicalSpecialty.CHIRURGIE_CARDIAQUE.getDisplayName()).contains("Chirurgie");
        assertThat(MedicalSpecialty.NEUROCHIRURGIE.getDisplayName()).contains("Neurochirurgie");
    }

    @Test
    void testPediatricSpecialty() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.PEDIATRIE;

        // Then
        assertThat(specialty.getDisplayName()).isEqualTo("Pédiatrie");
        assertThat(specialty.getDescription()).contains("Enfants");
    }

    @Test
    void testGeriatricSpecialty() {
        // When
        MedicalSpecialty specialty = MedicalSpecialty.GERIATRIE;

        // Then
        assertThat(specialty.getDisplayName()).isEqualTo("Gériatrie");
        assertThat(specialty.getDescription()).contains("Personnes âgées");
    }
}
