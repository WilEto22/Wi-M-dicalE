import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Paper,
  Link,
  AppBar,
  Toolbar,
} from '@mui/material';
import { LocalHospital } from '@mui/icons-material';
import RegisterForm from '../components/auth/RegisterForm';

const Register = () => {
  return (
    <Box
      sx={{
        flexGrow: 1,
        minHeight: '100vh',
        position: 'relative',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundImage: 'url(/images/medical-bg.jpg)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          opacity: 0.3,
          zIndex: 0,
        },
      }}
    >
      <AppBar position="static" sx={{ position: 'relative', zIndex: 1 }}>
        <Toolbar>
          <LocalHospital sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Wi-M-dicalE
          </Typography>
        </Toolbar>
      </AppBar>

      <Container component="main" maxWidth="sm" sx={{ position: 'relative', zIndex: 1 }}>
        <Box
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Paper
            elevation={3}
            sx={{
              p: 4,
              width: '100%',
              bgcolor: 'rgba(255, 255, 255, 0.3)', // Même transparence que Login
              backdropFilter: 'blur(3px)', // Flou minimal
              boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.05)', // Ombre très légère
              border: '1px solid rgba(255, 255, 255, 0.4)',
            }}
          >
            <Typography component="h1" variant="h5" align="center" gutterBottom>
              Inscription
            </Typography>
            <RegisterForm />
            <Box sx={{ mt: 2, textAlign: 'center' }}>
              <Link component={RouterLink} to="/login" variant="body2">
                Déjà un compte ? Se connecter
              </Link>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
};

export default Register;
