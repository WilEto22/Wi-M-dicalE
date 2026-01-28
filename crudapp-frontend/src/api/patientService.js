import axios from './axios.config';

export const getAllPatients = async (page = 0, size = 10) => {
  const response = await axios.get('/patients', {
    params: { page, size },
  });
  return response.data;
};

export const getPatientById = async (id) => {
  const response = await axios.get(`/patients/${id}`);
  return response.data;
};

export const createPatient = async (patientData) => {
  const response = await axios.post('/patients', patientData);
  return response.data;
};

export const updatePatient = async (id, patientData) => {
  const response = await axios.put(`/patients/${id}`, patientData);
  return response.data;
};

export const deletePatient = async (id) => {
  const response = await axios.delete(`/patients/${id}`);
  return response.data;
};

export const searchPatients = async (criteria, page = 0, size = 10) => {
  const response = await axios.get('/patients/search', {
    params: { ...criteria, page, size },
  });
  return response.data;
};

export const exportPatientsExcel = async () => {
  const response = await axios.get('/patients/export/excel', {
    responseType: 'blob',
  });
  return response.data;
};

export const exportPatientsPDF = async () => {
  const response = await axios.get('/patients/export/pdf', {
    responseType: 'blob',
  });
  return response.data;
};
