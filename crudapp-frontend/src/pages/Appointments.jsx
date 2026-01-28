import React from 'react';
import {
  Container,
  Box,
  Typography,
  AppBar,
  Toolbar,
  Button,
} from '@mui/material';
import { LocalHospital } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { logout } from '../store/slices/authSlice';

const Appointments = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <LocalHospital sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Wi-M-dicalE
          </Typography>
          <Button color="inherit" onClick={() => navigate('/dashboard')}>
            Tableau de bord
          </Button>
          <Button color="inherit" onClick={() => navigate('/patients')}>
            Patients
          </Button>
          <Button color="inherit" onClick={() => navigate('/profile')}>
            Profil
          </Button>
          <Button color="inherit" onClick={handleLogout}>
            Déconnexion
          </Button>
        </Toolbar>
      </AppBar>

      <Container sx={{ py: 4 }} maxWidth="lg">
        <Typography variant="h4" gutterBottom>
          Gestion des Rendez-vous
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
          📅 Page en cours de développement...
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Cette page permettra de gérer les rendez-vous avec un calendrier interactif.
        </Typography>
      </Container>
    </Box>
  );
};

export default Appointments;
