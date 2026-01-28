import { useNavigate, useLocation } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Divider,
} from '@mui/material';
import {
  Dashboard,
  People,
  CalendarMonth,
  LocalHospital,
  Person,
} from '@mui/icons-material';
import { ROUTES, USER_ROLES } from '../../utils/constants';
import { tokenManager } from '../../utils/tokenManager';

const drawerWidth = 240;

const Sidebar = ({ open, onClose }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const user = tokenManager.getUserFromToken();

  const menuItems = [
    {
      text: 'Dashboard',
      icon: <Dashboard />,
      path: ROUTES.DASHBOARD,
      roles: [USER_ROLES.PATIENT, USER_ROLES.DOCTOR, USER_ROLES.ADMIN],
    },
    {
      text: 'Patients',
      icon: <People />,
      path: ROUTES.PATIENTS,
      roles: [USER_ROLES.DOCTOR, USER_ROLES.ADMIN],
    },
    {
      text: 'Rendez-vous',
      icon: <CalendarMonth />,
      path: ROUTES.APPOINTMENTS,
      roles: [USER_ROLES.PATIENT, USER_ROLES.DOCTOR, USER_ROLES.ADMIN],
    },
    {
      text: 'Médecins',
      icon: <LocalHospital />,
      path: ROUTES.DOCTORS,
      roles: [USER_ROLES.PATIENT, USER_ROLES.ADMIN],
    },
    {
      text: 'Mon Profil',
      icon: <Person />,
      path: ROUTES.PROFILE,
      roles: [USER_ROLES.PATIENT, USER_ROLES.DOCTOR, USER_ROLES.ADMIN],
    },
  ];

  const filteredMenuItems = menuItems.filter((item) =>
    item.roles.some((role) => user?.roles?.includes(role))
  );

  const handleNavigation = (path) => {
    navigate(path);
    if (onClose) onClose();
  };

  return (
    <Drawer
      variant="temporary"
      open={open}
      onClose={onClose}
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSizing: 'border-box',
        },
      }}
    >
      <Toolbar />
      <Divider />
      <List>
        {filteredMenuItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              selected={location.pathname === item.path}
              onClick={() => handleNavigation(item.path)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Drawer>
  );
};

export default Sidebar;
