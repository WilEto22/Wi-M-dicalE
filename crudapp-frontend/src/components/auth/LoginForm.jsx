import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  TextField,
  Button,
  CircularProgress,
  Alert,
} from '@mui/material';
import { login, getCurrentUser } from '../../store/slices/authSlice';

const schema = yup.object({
  username: yup.string().required('Le nom d\'utilisateur est requis'),
  password: yup.string().required('Le mot de passe est requis'),
});

const LoginForm = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
  });

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      setError('');
      const result = await dispatch(login(data)).unwrap();

      // Récupérer les informations de l'utilisateur pour rediriger selon le rôle
      const userInfoResult = await dispatch(getCurrentUser()).unwrap();

      // Rediriger selon le type d'utilisateur
      if (userInfoResult.userType === 'DOCTOR') {
        navigate('/dashboard'); // Dashboard médecin
      } else if (userInfoResult.userType === 'PATIENT') {
        navigate('/patients'); // Page patients
      } else {
        navigate('/dashboard'); // Par défaut
      }
    } catch (err) {
      setError(err || 'Erreur de connexion');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <TextField
        margin="normal"
        required
        fullWidth
        id="username"
        label="Nom d'utilisateur"
        name="username"
        autoComplete="username"
        autoFocus
        {...register('username')}
        error={!!errors.username}
        helperText={errors.username?.message}
        sx={{
          '& .MuiOutlinedInput-root': {
            '&.Mui-error .MuiOutlinedInput-notchedOutline': {
              borderColor: 'rgba(211, 47, 47, 0.3)', // Bordure rouge très atténuée
              borderWidth: '1px', // Bordure fine
            },
            '&.Mui-error:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: 'rgba(211, 47, 47, 0.4)', // Légèrement plus visible au survol
            },
          },
          '& .MuiFormHelperText-root.Mui-error': {
            color: 'rgba(211, 47, 47, 0.6)', // Texte d'erreur atténué
          },
        }}
      />

      <TextField
        margin="normal"
        required
        fullWidth
        name="password"
        label="Mot de passe"
        type="password"
        id="password"
        autoComplete="current-password"
        {...register('password')}
        error={!!errors.password}
        helperText={errors.password?.message}
        sx={{
          '& .MuiOutlinedInput-root': {
            '&.Mui-error .MuiOutlinedInput-notchedOutline': {
              borderColor: 'rgba(211, 47, 47, 0.3)', // Bordure rouge très atténuée
              borderWidth: '1px', // Bordure fine
            },
            '&.Mui-error:hover .MuiOutlinedInput-notchedOutline': {
              borderColor: 'rgba(211, 47, 47, 0.4)', // Légèrement plus visible au survol
            },
          },
          '& .MuiFormHelperText-root.Mui-error': {
            color: 'rgba(211, 47, 47, 0.6)', // Texte d'erreur atténué
          },
        }}
      />

      <Button
        type="submit"
        fullWidth
        variant="contained"
        sx={{ mt: 3, mb: 2 }}
        disabled={loading}
      >
        {loading ? <CircularProgress size={24} /> : 'Se connecter'}
      </Button>
    </Box>
  );
};

export default LoginForm;
