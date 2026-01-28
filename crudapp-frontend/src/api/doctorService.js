import axios from './axios.config';

export const getAllDoctors = async () => {
  const response = await axios.get('/doctors');
  return response.data;
};

export const getDoctorById = async (id) => {
  const response = await axios.get(`/doctors/${id}`);
  return response.data;
};

export const getDoctorAvailability = async (doctorId, date) => {
  const response = await axios.get(`/doctors/${doctorId}/availability`, {
    params: { date },
  });
  return response.data;
};

export const setDoctorAvailability = async (availabilityData) => {
  const response = await axios.post('/doctors/my-availability', availabilityData);
  return response.data;
};
