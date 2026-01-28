import { format, parseISO } from 'date-fns';
import { fr } from 'date-fns/locale';

/**
 * Formater une date
 */
export const formatDate = (date, formatStr = 'dd/MM/yyyy') => {
  if (!date) return '';
  try {
    const dateObj = typeof date === 'string' ? parseISO(date) : date;
    return format(dateObj, formatStr, { locale: fr });
  } catch (error) {
    console.error('Error formatting date:', error);
    return '';
  }
};

/**
 * Formater une date et heure
 */
export const formatDateTime = (date) => {
  return formatDate(date, 'dd/MM/yyyy HH:mm');
};

/**
 * Formater une heure
 */
export const formatTime = (date) => {
  return formatDate(date, 'HH:mm');
};

/**
 * Formater un numéro de téléphone
 */
export const formatPhoneNumber = (phone) => {
  if (!phone) return '';
  // Format: 06 12 34 56 78
  return phone.replace(/(\d{2})(?=\d)/g, '$1 ').trim();
};

/**
 * Formater un nom complet
 */
export const formatFullName = (firstName, lastName) => {
  return `${firstName || ''} ${lastName || ''}`.trim();
};

/**
 * Formater une spécialité médicale
 */
export const formatSpecialty = (specialty) => {
  if (!specialty) return '';
  return specialty
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());
};

/**
 * Formater un statut de rendez-vous
 */
export const formatAppointmentStatus = (status) => {
  const statusMap = {
    SCHEDULED: 'Planifié',
    CONFIRMED: 'Confirmé',
    CANCELLED: 'Annulé',
    COMPLETED: 'Terminé',
  };
  return statusMap[status] || status;
};

/**
 * Formater un rôle utilisateur
 */
export const formatUserRole = (role) => {
  const roleMap = {
    PATIENT: 'Patient',
    DOCTOR: 'Médecin',
    ADMIN: 'Administrateur',
  };
  return roleMap[role] || role;
};

/**
 * Télécharger un fichier blob
 */
export const downloadFile = (blob, filename) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
};
