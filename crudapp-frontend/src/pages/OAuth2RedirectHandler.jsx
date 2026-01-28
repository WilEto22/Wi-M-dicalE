import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CircularProgress, Box, Typography } from '@mui/material';
import { setToken, setUser } from '../utils/tokenManager';

const OAuth2RedirectHandler = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get('token');
    const error = searchParams.get('error');

    if (token) {
      // Extract user info from URL params
      const email = searchParams.get('email');
      const name = searchParams.get('name');
      const role = searchParams.get('role');

      // Store token in localStorage
      setToken(token);

      // Store user info in localStorage
      const user = {
        email,
        username: name,
        fullName: name,
        userType: role
      };
      setUser(user);

      // Redirect to dashboard
      navigate('/dashboard');
    } else if (error) {
      // Redirect to login with error
      navigate(`/login?error=${encodeURIComponent(error)}`);
    } else {
      // No token or error, redirect to login
      navigate('/login');
    }
  }, [searchParams, navigate]);

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        gap: 2
      }}
    >
      <CircularProgress size={60} />
      <Typography variant="h6">Connexion en cours...</Typography>
    </Box>
  );
};

export default OAuth2RedirectHandler;
