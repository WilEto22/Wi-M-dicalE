package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorResponseTest {

    @Test
    void testBuilder() {
        // When
        DoctorResponse response = DoctorResponse.builder()
                .id(1L)
                .username("dr.martin")
                .fullName("Dr. Martin")
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .specialtyDisplay("Cardiologie")
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("dr.martin");
        assertThat(response.getFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testFromUser() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("dr.martin")
                .fullName("Dr. Martin")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        // When
        DoctorResponse response = DoctorResponse.fromUser(user);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("dr.martin");
        assertThat(response.getFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testFromUserWithNullSpecialty() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("dr.martin")
                .fullName("Dr. Martin")
                .userType(UserType.DOCTOR)
                .specialty(null)
                .build();

        // When
        DoctorResponse response = DoctorResponse.fromUser(user);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("dr.martin");
        assertThat(response.getSpecialty()).isNull();
        assertThat(response.getSpecialtyDisplay()).isNull();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorResponse response = new DoctorResponse();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        DoctorResponse response = new DoctorResponse(
                1L,
                "dr.martin",
                "Dr. Martin",
                MedicalSpecialty.CARDIOLOGIE,
                "Cardiologie"
        );

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("dr.martin");
        assertThat(response.getFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorResponse response = new DoctorResponse();

        // When
        response.setId(1L);
        response.setUsername("dr.martin");
        response.setFullName("Dr. Martin");
        response.setSpecialty(MedicalSpecialty.DERMATOLOGIE);
        response.setSpecialtyDisplay("Dermatologie");

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("dr.martin");
        assertThat(response.getFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getSpecialty()).isEqualTo(MedicalSpecialty.DERMATOLOGIE);
        assertThat(response.getSpecialtyDisplay()).isEqualTo("Dermatologie");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        DoctorResponse response1 = DoctorResponse.builder()
                .id(1L)
                .username("dr.martin")
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        DoctorResponse response2 = DoctorResponse.builder()
                .id(1L)
                .username("dr.martin")
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        DoctorResponse response = DoctorResponse.builder()
                .id(1L)
                .username("dr.martin")
                .build();

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("dr.martin");
    }
}
