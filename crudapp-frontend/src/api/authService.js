import axios from './axios.config';

export const login = async (credentials) => {
  const response = await axios.post('/auth/login', credentials);
  return response.data;
};

export const register = async (userData) => {
  console.log('🌐 authService.register - Envoi vers API:', userData);
  const response = await axios.post('/auth/register', userData);
  console.log('✅ authService.register - Réponse reçue:', response.data);
  return response.data;
};

export const logout = async () => {
  const response = await axios.post('/auth/logout');
  return response.data;
};

export const refreshToken = async (refreshToken) => {
  const response = await axios.post('/auth/refresh', { refreshToken });
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await axios.get('/auth/me');
  return response.data;
};

export const updateProfile = async (profileData) => {
  console.log('🌐 authService.updateProfile - Envoi vers API:', profileData);
  const response = await axios.put('/auth/profile', profileData);
  console.log('✅ authService.updateProfile - Réponse reçue:', response.data);
  return response.data;
};

export const uploadProfilePhoto = async (file) => {
  const formData = new FormData();
  formData.append('file', file);

  console.log('🌐 authService.uploadProfilePhoto - Upload de fichier');
  const response = await axios.post('/files/upload-profile-photo', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  console.log('✅ authService.uploadProfilePhoto - Réponse reçue:', response.data);
  return response.data;
};
