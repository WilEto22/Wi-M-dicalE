import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import * as authService from '../../api/authService';
import { setToken, removeToken, getToken, setUser, getUser } from '../../utils/tokenManager';

// Thunks
export const login = createAsyncThunk(
  'auth/login',
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await authService.login(credentials);
      setToken(response.accessToken);
      return response;
    } catch (error) {
      // Le backend renvoie un objet ErrorResponse avec { status, message, errors, timestamp }
      const errorMessage = error.response?.data?.message || 'Erreur de connexion';
      return rejectWithValue(errorMessage);
    }
  }
);

export const register = createAsyncThunk(
  'auth/register',
  async (userData, { rejectWithValue }) => {
    try {
      const response = await authService.register(userData);
      setToken(response.accessToken);
      return response;
    } catch (error) {
      // Le backend renvoie un objet ErrorResponse avec { status, message, errors, timestamp }
      const errorMessage = error.response?.data?.message || 'Erreur lors de l\'inscription';
      const errorDetails = error.response?.data?.errors || null;

      // Si des erreurs de validation existent, les formater
      if (errorDetails) {
        const formattedErrors = Object.entries(errorDetails)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join(', ');
        return rejectWithValue(`${errorMessage} - ${formattedErrors}`);
      }

      return rejectWithValue(errorMessage);
    }
  }
);

export const getCurrentUser = createAsyncThunk(
  'auth/getCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const response = await authService.getCurrentUser();
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération de l\'utilisateur');
    }
  }
);

// Slice
const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: getUser(),
    token: getToken(),
    isAuthenticated: !!getToken(),
    loading: false,
    error: null,
  },
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      removeToken();
    },
    clearError: (state) => {
      state.error = null;
    },
    updateUser: (state, action) => {
      state.user = action.payload;
      setUser(action.payload);
    },
  },
  extraReducers: (builder) => {
    builder
      // Login
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.isAuthenticated = true;
        state.token = action.payload.accessToken;
        state.user = {
          username: action.payload.username,
          userType: action.payload.userType,
        };
        setUser(state.user);
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Register
      .addCase(register.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(register.fulfilled, (state, action) => {
        state.loading = false;
        state.isAuthenticated = true;
        state.token = action.payload.accessToken;
        state.user = {
          username: action.payload.username,
          userType: action.payload.userType,
        };
        setUser(state.user);
      })
      .addCase(register.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Get Current User
      .addCase(getCurrentUser.pending, (state) => {
        state.loading = true;
      })
      .addCase(getCurrentUser.fulfilled, (state, action) => {
        state.loading = false;
        state.user = action.payload;
      })
      .addCase(getCurrentUser.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { logout, clearError, updateUser } = authSlice.actions;
export default authSlice.reducer;
