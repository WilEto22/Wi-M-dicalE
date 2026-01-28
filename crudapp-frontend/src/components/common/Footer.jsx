import { Box, Typography, Link } from '@mui/material';
import { APP_NAME, APP_VERSION } from '../../utils/constants';

const Footer = () => {
  return (
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
      <Typography variant="body2" color="text.secondary" align="center">
        © {new Date().getFullYear()} {APP_NAME} - Version {APP_VERSION}
      </Typography>
      <Typography variant="body2" color="text.secondary" align="center">
        Développé avec ❤️ pour la gestion médicale
      </Typography>
    </Box>
  );
};

export default Footer;
