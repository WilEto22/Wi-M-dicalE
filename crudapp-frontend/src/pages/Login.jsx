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
  Button,
  Divider,
} from '@mui/material';
import { LocalHospital, Google } from '@mui/icons-material';
import LoginForm from '../components/auth/LoginForm';

const Login = () => {
  const handleOAuth2Login = (provider) => {
    // Redirect to backend OAuth2 endpoint
    window.location.href = `http://localhost:8080/oauth2/authorization/${provider}`;
  };

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
          backgroundImage: 'url(/images/wi-medicale.png)',
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

      <Container component="main" maxWidth="xs" sx={{ position: 'relative', zIndex: 1, maxWidth: '280px' }}>
        <Box
          sx={{
            marginTop: 0.5,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
          }}
        >
          <Paper
            elevation={3}
            sx={{
              p: 0.8,
              width: '100%',
              maxWidth: '240px',
              bgcolor: 'rgba(255, 255, 255, 0.3)', // Encore plus transparent
              backdropFilter: 'blur(3px)', // Flou minimal
              boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.05)', // Ombre très légère
              border: '1px solid rgba(255, 255, 255, 0.4)',
            }}
          >
            <Typography component="h1" variant="h6" align="center" sx={{ mb: 0.2, fontSize: '0.95rem' }}>
              Connexion
            </Typography>
            <LoginForm />

            <Divider sx={{ my: 1 }}>OU</Divider>

            <Button
              fullWidth
              variant="outlined"
              startIcon={<Google />}
              onClick={() => handleOAuth2Login('google')}
              sx={{
                textTransform: 'none',
                borderColor: '#DB4437',
                color: '#DB4437',
                fontSize: '0.75rem',
                py: 0.5,
                '&:hover': {
                  borderColor: '#DB4437',
                  bgcolor: 'rgba(219, 68, 55, 0.04)',
                },
              }}
            >
              Continuer avec Google
            </Button>

            <Box sx={{ mt: 0.2, textAlign: 'center' }}>
              <Link component={RouterLink} to="/register" variant="body2">
                Pas encore de compte ? S'inscrire
              </Link>
            </Box>
          </Paper>
        </Box>
      </Container>
    </Box>
  );
};

export default Login;
