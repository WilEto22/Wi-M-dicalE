package com.example.crudApp.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitaire pour calculer les jours ouvrables (du lundi au vendredi)
 * Exclut les week-ends et les jours fériés
 */
public class BusinessDayCalculator {

    // Jours fériés fixes en France (peut être configuré via base de données)
    private static final Set<LocalDate> FIXED_HOLIDAYS = new HashSet<>();

    static {
        // Exemple de jours fériés fixes pour 2023-2025
        // Jour de l'an
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 1, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 1, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 1, 1));

        // Fête du travail
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 5, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 5, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 5, 1));

        // Victoire 1945
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 5, 8));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 5, 8));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 5, 8));

        // Fête nationale
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 7, 14));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 7, 14));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 7, 14));

        // Assomption
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 8, 15));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 8, 15));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 8, 15));

        // Toussaint
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 11, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 11, 1));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 11, 1));

        // Armistice 1918
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 11, 11));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 11, 11));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 11, 11));

        // Noël
        FIXED_HOLIDAYS.add(LocalDate.of(2023, 12, 25));
        FIXED_HOLIDAYS.add(LocalDate.of(2024, 12, 25));
        FIXED_HOLIDAYS.add(LocalDate.of(2025, 12, 25));
    }

    /**
     * Vérifie si une date est un jour ouvrable (lundi-vendredi, hors jours fériés)
     */
    public static boolean isBusinessDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Week-end
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        // Jour férié
        if (FIXED_HOLIDAYS.contains(date)) {
            return false;
        }

        return true;
    }

    /**
     * Calcule le nombre de jours ouvrables entre deux dates (exclusif des deux bornes)
     * @param start Date de début (exclue)
     * @param end Date de fin (exclue)
     * @return Nombre de jours ouvrables entre les deux dates
     */
    public static long countBusinessDaysBetween(LocalDateTime start, LocalDateTime end) {
        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();

        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            return 0;
        }

        long businessDays = 0;
        LocalDate current = startDate.plusDays(1); // Exclure le jour de début

        while (current.isBefore(endDate)) { // Exclure aussi le jour de fin
            if (isBusinessDay(current)) {
                businessDays++;
            }
            current = current.plusDays(1);
        }

        return businessDays;
    }

    /**
     * Vérifie s'il y a au moins N jours ouvrables entre deux dates
     * @param start Date de début
     * @param end Date de fin
     * @param requiredBusinessDays Nombre de jours ouvrables requis
     * @return true si le nombre de jours ouvrables est suffisant
     */
    public static boolean hasAtLeastBusinessDays(LocalDateTime start, LocalDateTime end, int requiredBusinessDays) {
        return countBusinessDaysBetween(start, end) >= requiredBusinessDays;
    }

    /**
     * Calcule le nombre d'heures ouvrables entre deux dates
     * (en considérant 8h de travail par jour ouvrable)
     */
    public static long countBusinessHoursBetween(LocalDateTime start, LocalDateTime end) {
        long businessDays = countBusinessDaysBetween(start, end);

        // Ajouter les heures du jour de début et de fin si ce sont des jours ouvrables
        long additionalHours = 0;

        if (isBusinessDay(start.toLocalDate())) {
            // Heures restantes dans le jour de début (jusqu'à 18h)
            int startHour = start.getHour();
            if (startHour < 18) {
                additionalHours += Math.min(18 - startHour, ChronoUnit.HOURS.between(start, end));
            }
        }

        if (isBusinessDay(end.toLocalDate()) && !start.toLocalDate().equals(end.toLocalDate())) {
            // Heures dans le jour de fin (depuis 9h)
            int endHour = end.getHour();
            if (endHour > 9) {
                additionalHours += Math.min(endHour - 9, 8);
            }
        }

        return (businessDays * 8) + additionalHours;
    }

    /**
     * Ajoute un jour férié personnalisé
     */
    public static void addHoliday(LocalDate holiday) {
        FIXED_HOLIDAYS.add(holiday);
    }

    /**
     * Supprime un jour férié
     */
    public static void removeHoliday(LocalDate holiday) {
        FIXED_HOLIDAYS.remove(holiday);
    }
}
