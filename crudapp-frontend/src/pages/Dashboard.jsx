import React, { useEffect } from 'react';
import {
  Container,
  Box,
  Typography,
  Grid,
  Card,
  CardContent,
  AppBar,
  Toolbar,
  Button,
  CircularProgress,
} from '@mui/material';
import {
  LocalHospital,
  People,
  CalendarMonth,
  TrendingUp,
  MedicalServices,
  EventAvailable,
  PersonAdd,
  AccountCircle,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout, getCurrentUser } from '../store/slices/authSlice';

const Dashboard = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user, loading } = useSelector((state) => state.auth);

  useEffect(() => {
    // Charger les informations de l'utilisateur si elles ne sont pas déjà présentes
    if (!user) {
      dispatch(getCurrentUser());
    }
  }, [dispatch, user]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };

  // Afficher un loader pendant le chargement
  if (loading || !user) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  const isDoctor = user.userType === 'DOCTOR' || user.userType === 'Médecin' || user.role === 'DOCTOR';
  const isPatient = user.userType === 'PATIENT' || user.userType === 'Patient' || user.role === 'PATIENT';

  return (
    <Box sx={{ flexGrow: 1, minHeight: '100vh', bgcolor: '#BBDEFB' }}>
      <AppBar position="static">
        <Toolbar>
          <LocalHospital sx={{ mr: 2 }} />
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Wi-M-dicalE - Tableau de bord
          </Typography>
          {isDoctor && (
            <Button color="inherit" onClick={() => navigate('/patients')}>
              Patients
            </Button>
          )}
          <Button color="inherit" onClick={() => navigate('/appointments')}>
            Rendez-vous
          </Button>
          <Button color="inherit" onClick={() => navigate('/profile')}>
            Profil
          </Button>
          <Button color="inherit" onClick={handleLogout}>
            Déconnexion
          </Button>
          {isDoctor && user.profilePhoto ? (
            <Box
              component="img"
              src={user.profilePhoto}
              alt="Photo de profil"
              sx={{
                width: 36,
                height: 36,
                borderRadius: '50%',
                objectFit: 'cover',
                ml: 2,
                border: '2px solid white',
                boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
                cursor: 'pointer',
              }}
              onClick={() => navigate('/profile')}
              onError={(e) => {
                e.target.style.display = 'none';
              }}
            />
          ) : isDoctor ? (
            <AccountCircle
              sx={{ fontSize: 36, ml: 2, cursor: 'pointer' }}
              onClick={() => navigate('/profile')}
            />
          ) : null}
        </Toolbar>
      </AppBar>

      <Container sx={{ py: 4 }} maxWidth="lg">
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: 'primary.main' }}>
          Tableau de bord {isDoctor ? 'Docteur' : 'Patient'}
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          Bienvenue, {isDoctor ? `Dr. ${user.fullName || user.username}` : (user.fullName || user.username)} !
        </Typography>

        {/* Dashboard pour DOCTOR */}
        {isDoctor && (
          <>
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'primary.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <People sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Mes Patients
                        </Typography>
                        <Typography variant="h4">0</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/patients')}
                    >
                      Gérer les patients
                    </Button>
                  </CardContent>
                </Card>
              </Grid>

              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'secondary.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <CalendarMonth sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Rendez-vous
                        </Typography>
                        <Typography variant="h4">0</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/appointments')}
                    >
                      Voir l'agenda
                    </Button>
                  </CardContent>
                </Card>
              </Grid>

              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'success.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <MedicalServices sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Spécialité
                        </Typography>
                        <Typography variant="h6">{user.specialty || 'N/A'}</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/profile')}
                    >
                      Mon profil
                    </Button>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>

            <Box sx={{ mt: 4 }}>
              <Typography variant="h5" gutterBottom>
                Bienvenue Dr. {user.fullName || user.username}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Gérez vos patients, consultez votre agenda et suivez vos rendez-vous depuis ce tableau de bord.
              </Typography>
            </Box>
          </>
        )}

        {/* Dashboard pour PATIENT */}
        {isPatient && (
          <>
            <Grid container spacing={3} sx={{ mt: 2 }}>
              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'info.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <EventAvailable sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Mes Rendez-vous
                        </Typography>
                        <Typography variant="h4">0</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/appointments')}
                    >
                      Voir mes RDV
                    </Button>
                  </CardContent>
                </Card>
              </Grid>

              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'warning.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <PersonAdd sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Nouveau RDV
                        </Typography>
                        <Typography variant="body2">Prendre rendez-vous</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/appointments')}
                    >
                      Réserver
                    </Button>
                  </CardContent>
                </Card>
              </Grid>

              <Grid item xs={12} md={4}>
                <Card sx={{ bgcolor: 'success.light', color: 'white' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <TrendingUp sx={{ fontSize: 40, mr: 2 }} />
                      <Box>
                        <Typography variant="h6">
                          Mon Profil
                        </Typography>
                        <Typography variant="body2">Informations personnelles</Typography>
                      </Box>
                    </Box>
                    <Button
                      variant="contained"
                      color="inherit"
                      fullWidth
                      onClick={() => navigate('/profile')}
                    >
                      Voir mon profil
                    </Button>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>

            <Box sx={{ mt: 4 }}>
              <Typography variant="h5" gutterBottom>
                Bienvenue {user.fullName || user.username}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                Consultez vos rendez-vous, prenez de nouveaux rendez-vous et gérez votre profil médical.
              </Typography>
            </Box>
          </>
        )}
      </Container>
    </Box>
  );
};

export default Dashboard;
