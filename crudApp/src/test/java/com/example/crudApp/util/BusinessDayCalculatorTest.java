package com.example.crudApp.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour BusinessDayCalculator
 */
class BusinessDayCalculatorTest {

    @Test
    void testIsBusinessDay_Monday() {
        // Lundi 25 décembre 2023 (mais c'est Noël, donc férié)
        LocalDate christmas = LocalDate.of(2023, 12, 25);
        assertFalse(BusinessDayCalculator.isBusinessDay(christmas), "Noël devrait être un jour férié");

        // Lundi 18 décembre 2023 (jour ouvrable normal)
        LocalDate normalMonday = LocalDate.of(2023, 12, 18);
        assertTrue(BusinessDayCalculator.isBusinessDay(normalMonday), "Lundi normal devrait être un jour ouvrable");
    }

    @Test
    void testIsBusinessDay_Weekend() {
        // Samedi
        LocalDate saturday = LocalDate.of(2024, 12, 21);
        assertFalse(BusinessDayCalculator.isBusinessDay(saturday), "Samedi ne devrait pas être un jour ouvrable");

        // Dimanche
        LocalDate sunday = LocalDate.of(2024, 12, 22);
        assertFalse(BusinessDayCalculator.isBusinessDay(sunday), "Dimanche ne devrait pas être un jour ouvrable");
    }

    @Test
    void testIsBusinessDay_Holidays() {
        // 1er janvier
        LocalDate newYear = LocalDate.of(2024, 1, 1);
        assertFalse(BusinessDayCalculator.isBusinessDay(newYear), "1er janvier devrait être un jour férié");

        // 1er mai
        LocalDate laborDay = LocalDate.of(2024, 5, 1);
        assertFalse(BusinessDayCalculator.isBusinessDay(laborDay), "1er mai devrait être un jour férié");

        // 14 juillet
        LocalDate bastilleDay = LocalDate.of(2024, 7, 14);
        assertFalse(BusinessDayCalculator.isBusinessDay(bastilleDay), "14 juillet devrait être un jour férié");
    }

    @Test
    void testCountBusinessDaysBetween_SameWeek() {
        // Du lundi au vendredi (même semaine)
        LocalDateTime monday = LocalDateTime.of(2024, 12, 16, 9, 0);
        LocalDateTime friday = LocalDateTime.of(2024, 12, 20, 17, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(monday, friday);
        // Entre lundi et vendredi (exclusif): mardi, mercredi, jeudi = 3 jours
        assertEquals(3, businessDays, "Du lundi au vendredi devrait compter 3 jours ouvrables (excluant lundi et vendredi)");
    }

    @Test
    void testCountBusinessDaysBetween_WithWeekend() {
        // Du vendredi au lundi suivant
        LocalDateTime friday = LocalDateTime.of(2024, 12, 20, 17, 0);
        LocalDateTime monday = LocalDateTime.of(2024, 12, 23, 9, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(friday, monday);
        assertEquals(0, businessDays, "Du vendredi au lundi suivant devrait compter 0 jours ouvrables (week-end)");
    }

    @Test
    void testCountBusinessDaysBetween_WithHoliday() {
        // Du 22 décembre au 26 décembre (incluant Noël le 25)
        LocalDateTime before = LocalDateTime.of(2024, 12, 22, 9, 0);
        LocalDateTime after = LocalDateTime.of(2024, 12, 26, 17, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(before, after);
        // Entre 22 et 26 (exclusif): 23 (lundi), 24 (mardi), 25 (Noël - férié)
        // Jours ouvrables: 23, 24 = 2 jours
        assertEquals(2, businessDays, "Devrait compter 2 jours ouvrables (excluant Noël)");
    }

    @Test
    void testCountBusinessDaysBetween_OneDayApart() {
        // Mardi au mercredi
        LocalDateTime tuesday = LocalDateTime.of(2024, 12, 17, 10, 0);
        LocalDateTime wednesday = LocalDateTime.of(2024, 12, 18, 10, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(tuesday, wednesday);
        assertEquals(0, businessDays, "Entre mardi et mercredi (1 jour) devrait compter 0 jours ouvrables complets");
    }

    @Test
    void testCountBusinessDaysBetween_TwoDaysApart() {
        // Lundi au mercredi
        LocalDateTime monday = LocalDateTime.of(2024, 12, 16, 10, 0);
        LocalDateTime wednesday = LocalDateTime.of(2024, 12, 18, 10, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(monday, wednesday);
        // Entre lundi et mercredi (exclusif): mardi = 1 jour
        assertEquals(1, businessDays, "Entre lundi et mercredi devrait compter 1 jour ouvrable (mardi)");
    }

    @Test
    void testCountBusinessDaysBetween_StartAfterEnd() {
        // Date de fin avant date de début
        LocalDateTime later = LocalDateTime.of(2024, 12, 20, 10, 0);
        LocalDateTime earlier = LocalDateTime.of(2024, 12, 16, 10, 0);

        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(later, earlier);
        assertEquals(0, businessDays, "Si la date de début est après la date de fin, devrait retourner 0");
    }

    @Test
    void testHasAtLeastBusinessDays_True() {
        // Du lundi au vendredi (3 jours ouvrables entre les deux)
        LocalDateTime monday = LocalDateTime.of(2024, 12, 16, 9, 0);
        LocalDateTime friday = LocalDateTime.of(2024, 12, 20, 17, 0);

        assertTrue(BusinessDayCalculator.hasAtLeastBusinessDays(monday, friday, 3),
                "Devrait avoir au moins 3 jours ouvrables");
        assertFalse(BusinessDayCalculator.hasAtLeastBusinessDays(monday, friday, 4),
                "Ne devrait pas avoir 4 jours ouvrables");
    }

    @Test
    void testHasAtLeastBusinessDays_False() {
        // Du lundi au mercredi (1 jour ouvrable)
        LocalDateTime monday = LocalDateTime.of(2024, 12, 16, 9, 0);
        LocalDateTime wednesday = LocalDateTime.of(2024, 12, 18, 17, 0);

        assertFalse(BusinessDayCalculator.hasAtLeastBusinessDays(monday, wednesday, 2),
                "Ne devrait pas avoir 2 jours ouvrables");
    }

    @Test
    void testHasAtLeastBusinessDays_ExactlyOne() {
        // Du lundi au mercredi (1 jour ouvrable: mardi)
        LocalDateTime monday = LocalDateTime.of(2024, 12, 16, 9, 0);
        LocalDateTime wednesday = LocalDateTime.of(2024, 12, 18, 17, 0);

        assertTrue(BusinessDayCalculator.hasAtLeastBusinessDays(monday, wednesday, 1),
                "Devrait avoir exactement 1 jour ouvrable");
    }

    @Test
    void testAddAndRemoveHoliday() {
        // Utiliser un samedi pour éviter les conflits avec les jours fériés existants
        LocalDate customHoliday = LocalDate.of(2024, 6, 17); // Lundi

        // Vérifier que c'est un jour ouvrable avant
        assertTrue(BusinessDayCalculator.isBusinessDay(customHoliday),
                "Devrait être un jour ouvrable avant ajout");

        // Ajouter un jour férié personnalisé
        BusinessDayCalculator.addHoliday(customHoliday);

        assertFalse(BusinessDayCalculator.isBusinessDay(customHoliday),
                "Le jour férié ajouté ne devrait pas être un jour ouvrable");

        // Retirer le jour férié
        BusinessDayCalculator.removeHoliday(customHoliday);

        assertTrue(BusinessDayCalculator.isBusinessDay(customHoliday),
                "Après suppression, devrait redevenir un jour ouvrable");
    }

    @Test
    void testRealWorldScenario_AppointmentCancellation() {
        // Scénario réel: Rendez-vous le mercredi 18 décembre à 14h
        LocalDateTime appointmentTime = LocalDateTime.of(2024, 12, 18, 14, 0);

        // Tentative d'annulation le lundi 16 décembre à 10h
        LocalDateTime cancellationAttempt1 = LocalDateTime.of(2024, 12, 16, 10, 0);
        long businessDays1 = BusinessDayCalculator.countBusinessDaysBetween(cancellationAttempt1, appointmentTime);
        assertTrue(businessDays1 >= 1, "Devrait avoir au moins 1 jour ouvrable (mardi 17)");

        // Tentative d'annulation le mardi 17 décembre à 16h
        LocalDateTime cancellationAttempt2 = LocalDateTime.of(2024, 12, 17, 16, 0);
        long businessDays2 = BusinessDayCalculator.countBusinessDaysBetween(cancellationAttempt2, appointmentTime);
        assertFalse(businessDays2 >= 1, "Ne devrait pas avoir 1 jour ouvrable complet");
    }

    @Test
    void testRealWorldScenario_WithWeekendAndHoliday() {
        // Rendez-vous le jeudi 26 décembre 2024 à 10h
        LocalDateTime appointmentTime = LocalDateTime.of(2024, 12, 26, 10, 0);

        // Tentative d'annulation le vendredi 20 décembre à 15h
        // Entre les deux (exclusif): samedi 21, dimanche 22, lundi 23, mardi 24, mercredi 25 (Noël)
        LocalDateTime cancellationAttempt = LocalDateTime.of(2024, 12, 20, 15, 0);
        long businessDays = BusinessDayCalculator.countBusinessDaysBetween(cancellationAttempt, appointmentTime);

        // Jours ouvrables entre 20 et 26 (exclusif): lundi 23, mardi 24 = 2 jours (mercredi 25 est Noël)
        assertEquals(2, businessDays, "Devrait compter 2 jours ouvrables (excluant week-end et Noël)");
    }
}
