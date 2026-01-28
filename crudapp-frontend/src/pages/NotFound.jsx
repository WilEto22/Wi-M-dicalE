import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Container, Box, Typography, Button } from '@mui/material';
import { Home } from '@mui/icons-material';

const NotFound = () => {
  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          textAlign: 'center',
        }}
      >
        <Typography variant="h1" component="h1" gutterBottom>
          404
        </Typography>
        <Typography variant="h4" component="h2" gutterBottom>
          Page non trouvée
        </Typography>
        <Typography variant="body1" color="text.secondary" paragraph>
          Désolé, la page que vous recherchez n'existe pas.
        </Typography>
        <Button
          variant="contained"
          component={RouterLink}
          to="/"
          startIcon={<Home />}
          sx={{ mt: 2 }}
        >
          Retour à l'accueil
        </Button>
      </Box>
    </Container>
  );
};

export default NotFound;
