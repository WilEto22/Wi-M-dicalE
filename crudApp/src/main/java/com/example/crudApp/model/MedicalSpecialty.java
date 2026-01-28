package com.example.crudApp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MedicalSpecialty {

    MEDECINE_GENERALE("Médecine générale", "Suivi global du patient, prévention, diagnostic de base"),
    CARDIOLOGIE("Cardiologie", "Cœur et vaisseaux sanguins"),
    PNEUMOLOGIE("Pneumologie", "Poumons et respiration"),
    GASTRO_ENTEROLOGIE("Gastro-entérologie", "Système digestif (estomac, intestins, foie)"),
    NEPHROLOGIE("Néphrologie", "Reins"),
    ENDOCRINOLOGIE("Endocrinologie", "Hormones (diabète, thyroïde)"),
    RHUMATOLOGIE("Rhumatologie", "Articulations, os, maladies inflammatoires"),
    NEUROLOGIE("Neurologie", "Cerveau et système nerveux"),
    DERMATOLOGIE("Dermatologie", "Peau, cheveux, ongles"),
    HEMATOLOGIE("Hématologie", "Maladies du sang"),
    ONCOLOGIE("Oncologie", "Cancers"),
    INFECTIOLOGIE("Infectiologie", "Maladies infectieuses"),
    ALLERGOLOGIE("Allergologie", "Allergies"),
    GERIATRIE("Gériatrie", "Personnes âgées"),
    PEDIATRIE("Pédiatrie", "Enfants et adolescents"),
    PSYCHIATRIE("Psychiatrie", "Troubles mentaux et psychiques"),
    CHIRURGIE_GENERALE("Chirurgie générale", "Chirurgie générale"),
    CHIRURGIE_ORTHOPEDIQUE("Chirurgie orthopédique", "Os, articulations"),
    NEUROCHIRURGIE("Neurochirurgie", "Cerveau et moelle épinière"),
    CHIRURGIE_CARDIAQUE("Chirurgie cardiaque", "Chirurgie du cœur"),
    CHIRURGIE_PLASTIQUE("Chirurgie plastique, reconstructrice et esthétique", "Chirurgie plastique et esthétique"),
    CHIRURGIE_VISCERALE("Chirurgie viscérale", "Chirurgie des organes abdominaux"),
    CHIRURGIE_PEDIATRIQUE("Chirurgie pédiatrique", "Chirurgie des enfants"),
    OPHTALMOLOGIE("Ophtalmologie", "Yeux et vision"),
    ORL("ORL (Oto-rhino-laryngologie)", "Oreilles, nez, gorge"),
    AUDIOLOGIE("Audiologie", "Audition"),
    RADIOLOGIE("Radiologie", "Imagerie médicale (IRM, scanner, radios)"),
    MEDECINE_NUCLEAIRE("Médecine nucléaire", "Imagerie et traitement par isotopes radioactifs"),
    ANATOMOPATHOLOGIE("Anatomopathologie", "Étude des tissus et cellules"),
    BIOLOGIE_MEDICALE("Biologie médicale", "Analyses biologiques"),
    MEDECINE_URGENCE("Médecine d'urgence", "Soins d'urgence"),
    MEDECINE_TRAVAIL("Médecine du travail", "Santé au travail"),
    MEDECINE_LEGALE("Médecine légale", "Médecine légale et expertises"),
    MEDECINE_SPORT("Médecine du sport", "Santé des sportifs"),
    MEDECINE_PHYSIQUE_READAPTATION("Médecine physique et de réadaptation", "Rééducation et réadaptation"),
    ANESTHESIE_REANIMATION("Anesthésie-réanimation", "Anesthésie et soins intensifs"),
    SANTE_PUBLIQUE("Santé publique", "Santé des populations");

    private final String displayName;
    private final String description;

    MedicalSpecialty(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // --------------------------
    // Conversion JSON pour Postman
    // --------------------------
    @JsonCreator
    public static MedicalSpecialty fromString(String value) {
        if (value == null) return null;
        switch (value.toUpperCase()) {
            case "CARDIOLOGY": return CARDIOLOGIE;
            case "DERMATOLOGY": return DERMATOLOGIE;
            default:
                for (MedicalSpecialty s : MedicalSpecialty.values()) {
                    if (s.name().equalsIgnoreCase(value) || s.displayName.equalsIgnoreCase(value)) {
                        return s;
                    }
                }
        }
        throw new IllegalArgumentException("Spécialité inconnue : " + value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
