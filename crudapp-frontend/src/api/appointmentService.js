import axios from './axios.config';

export const getAllAppointments = async (page = 0, size = 10) => {
  const response = await axios.get('/appointments', {
    params: { page, size },
  });
  return response.data;
};

export const getAppointmentById = async (id) => {
  const response = await axios.get(`/appointments/${id}`);
  return response.data;
};

export const createAppointment = async (appointmentData) => {
  const response = await axios.post('/appointments', appointmentData);
  return response.data;
};

export const updateAppointment = async (id, appointmentData) => {
  const response = await axios.put(`/appointments/${id}`, appointmentData);
  return response.data;
};

export const deleteAppointment = async (id) => {
  const response = await axios.delete(`/appointments/${id}`);
  return response.data;
};

export const getAppointmentsByPatient = async (patientId) => {
  const response = await axios.get(`/appointments/patient/${patientId}`);
  return response.data;
};

export const getAppointmentsByDoctor = async (doctorId) => {
  const response = await axios.get(`/appointments/doctor/${doctorId}`);
  return response.data;
};
