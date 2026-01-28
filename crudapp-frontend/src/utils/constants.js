// API Base URL
export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// App Info
export const APP_NAME = import.meta.env.VITE_APP_NAME || 'CrudApp Medical';
export const APP_VERSION = import.meta.env.VITE_APP_VERSION || '1.0.0';

// User Roles
export const USER_ROLES = {
  PATIENT: 'PATIENT',
  DOCTOR: 'DOCTOR',
  ADMIN: 'ADMIN',
};

// Appointment Status
export const APPOINTMENT_STATUS = {
  SCHEDULED: 'SCHEDULED',
  CONFIRMED: 'CONFIRMED',
  CANCELLED: 'CANCELLED',
  COMPLETED: 'COMPLETED',
};

// Medical Specialties
export const MEDICAL_SPECIALTIES = [
  'CARDIOLOGY',
  'DERMATOLOGY',
  'NEUROLOGY',
  'PEDIATRICS',
  'PSYCHIATRY',
  'RADIOLOGY',
  'SURGERY',
  'GENERAL_PRACTICE',
];

// Pagination
export const DEFAULT_PAGE_SIZE = 10;
export const PAGE_SIZE_OPTIONS = [5, 10, 20, 50, 100];

// Date Formats
export const DATE_FORMAT = 'dd/MM/yyyy';
export const DATETIME_FORMAT = 'dd/MM/yyyy HH:mm';
export const TIME_FORMAT = 'HH:mm';

// Local Storage Keys
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'accessToken',
  REFRESH_TOKEN: 'refreshToken',
  USER_INFO: 'userInfo',
};

// Routes
export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  DASHBOARD: '/dashboard',
  PATIENTS: '/patients',
  PATIENTS_NEW: '/patients/new',
  PATIENTS_EDIT: '/patients/:id/edit',
  PATIENTS_VIEW: '/patients/:id',
  APPOINTMENTS: '/appointments',
  APPOINTMENTS_NEW: '/appointments/new',
  APPOINTMENTS_EDIT: '/appointments/:id/edit',
  APPOINTMENTS_VIEW: '/appointments/:id',
  PROFILE: '/profile',
  DOCTORS: '/doctors',
  NOT_FOUND: '*',
};
