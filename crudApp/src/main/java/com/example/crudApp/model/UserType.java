package com.example.crudApp.model;

/**
 * Énumération des types d'utilisateurs dans le système
 */
public enum UserType {
    /**
     * Médecin - peut gérer des patients et des rendez-vous
     */
    DOCTOR("Médecin"),

    /**
     * Patient - peut prendre des rendez-vous avec des médecins
     */
    PATIENT("Patient");

    private final String displayName;

    UserType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
