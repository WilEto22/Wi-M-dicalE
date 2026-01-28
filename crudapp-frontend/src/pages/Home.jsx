import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
  Container,
  Box,
  Typography,
  Button,
  Grid,
  Card,
  CardContent,
  AppBar,
  Toolbar,
} from '@mui/material';
import {
  LocalHospital,
  People,
  CalendarMonth,
  MedicalServices,
} from '@mui/icons-material';

const Home = () => {
  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* Navbar */}
      <AppBar position="static">
        <Toolbar>
          <LocalHospital sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Wi-M-dicalE
          </Typography>
          <Button color="inherit" component={RouterLink} to="/login">
            Connexion
          </Button>
          <Button color="inherit" component={RouterLink} to="/register">
            Inscription
          </Button>
        </Toolbar>
      </AppBar>

      {/* Hero Section */}
      <Box
        sx={{
          bgcolor: 'primary.main',
          color: 'white',
          py: 8,
          textAlign: 'center',
        }}
      >
        <Container maxWidth="md">
          <Typography variant="h2" component="h1" gutterBottom>
            🏥 Bienvenue sur Wi-M-dicalE
          </Typography>
          <Typography variant="h5" paragraph>
            Votre plateforme de gestion médicale moderne et efficace
          </Typography>
          <Box sx={{ mt: 4 }}>
            <Button
              variant="contained"
              color="secondary"
              size="large"
              component={RouterLink}
              to="/register"
              sx={{ mr: 2 }}
            >
              Commencer
            </Button>
            <Button
              variant="outlined"
              color="inherit"
              size="large"
              component={RouterLink}
              to="/login"
            >
              Se connecter
            </Button>
          </Box>
        </Container>
      </Box>

      {/* Features Section */}
      <Container sx={{ py: 8 }} maxWidth="lg">
        <Typography variant="h3" align="center" gutterBottom>
          Fonctionnalités
        </Typography>
        <Grid container spacing={4} sx={{ mt: 2 }}>
          <Grid item xs={12} md={4}>
            <Card
              sx={{
                height: '100%',
                textAlign: 'center',
                p: 2,
                transition: 'all 0.3s ease-in-out',
                cursor: 'pointer',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: 6,
                },
              }}
            >
              <CardContent>
                <People sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                <Typography variant="h5" component="h2" gutterBottom>
                  Gestion des Patients
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Gérez facilement vos dossiers patients avec un système complet
                  de suivi médical
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              sx={{
                height: '100%',
                textAlign: 'center',
                p: 2,
                transition: 'all 0.3s ease-in-out',
                cursor: 'pointer',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: 6,
                },
              }}
            >
              <CardContent>
                <CalendarMonth sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                <Typography variant="h5" component="h2" gutterBottom>
                  Rendez-vous
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Planifiez et suivez vos consultations avec un calendrier
                  intuitif
                </Typography>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card
              sx={{
                height: '100%',
                textAlign: 'center',
                p: 2,
                transition: 'all 0.3s ease-in-out',
                cursor: 'pointer',
                '&:hover': {
                  transform: 'translateY(-10px)',
                  boxShadow: 6,
                },
              }}
            >
              <CardContent>
                <MedicalServices sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                <Typography variant="h5" component="h2" gutterBottom>
                  Médecins
                </Typography>
                <Typography variant="body1" color="text.secondary">
                  Gérez votre équipe médicale et leurs disponibilités
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>

      {/* Footer */}
      <Box
        component="footer"
        sx={{
          py: 3,
          px: 2,
          mt: 'auto',
          backgroundColor: (theme) =>
            theme.palette.mode === 'light'
              ? theme.palette.grey[200]
              : theme.palette.grey[800],
        }}
      >
        <Container maxWidth="sm">
          <Typography variant="body2" color="text.secondary" align="center">
            © 2024 Wi-M-dicalE - Tous droits réservés - Version 1.0.0
          </Typography>
        </Container>
      </Box>
    </Box>
  );
};

export default Home;
