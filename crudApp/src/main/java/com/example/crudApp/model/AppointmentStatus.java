package com.example.crudApp.model;

/**
 * Énumération des statuts de rendez-vous
 */
public enum AppointmentStatus {
    /**
     * Rendez-vous en attente de confirmation par le médecin
     */
    PENDING("En attente"),

    /**
     * Rendez-vous confirmé par le médecin
     */
    CONFIRMED("Confirmé"),

    /**
     * Rendez-vous annulé
     */
    CANCELLED("Annulé"),

    /**
     * Rendez-vous terminé
     */
    COMPLETED("Terminé");

    private final String displayName;

    AppointmentStatus(String displayName) {
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
