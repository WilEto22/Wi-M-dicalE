import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import * as appointmentService from '../../api/appointmentService';

// Thunks
export const fetchAppointments = createAsyncThunk(
  'appointments/fetchAll',
  async ({ page = 0, size = 10 }, { rejectWithValue }) => {
    try {
      const response = await appointmentService.getAllAppointments(page, size);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des rendez-vous');
    }
  }
);

export const fetchAppointmentById = createAsyncThunk(
  'appointments/fetchById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await appointmentService.getAppointmentById(id);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Rendez-vous non trouvé');
    }
  }
);

export const createAppointment = createAsyncThunk(
  'appointments/create',
  async (appointmentData, { rejectWithValue }) => {
    try {
      const response = await appointmentService.createAppointment(appointmentData);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création du rendez-vous');
    }
  }
);

export const updateAppointment = createAsyncThunk(
  'appointments/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      const response = await appointmentService.updateAppointment(id, data);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la mise à jour du rendez-vous');
    }
  }
);

export const deleteAppointment = createAsyncThunk(
  'appointments/delete',
  async (id, { rejectWithValue }) => {
    try {
      await appointmentService.deleteAppointment(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du rendez-vous');
    }
  }
);

// Slice
const appointmentSlice = createSlice({
  name: 'appointments',
  initialState: {
    appointments: [],
    currentAppointment: null,
    totalPages: 0,
    totalElements: 0,
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentAppointment: (state) => {
      state.currentAppointment = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all
      .addCase(fetchAppointments.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAppointments.fulfilled, (state, action) => {
        state.loading = false;
        state.appointments = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.totalElements = action.payload.totalElements || 0;
      })
      .addCase(fetchAppointments.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch by ID
      .addCase(fetchAppointmentById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAppointmentById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentAppointment = action.payload;
      })
      .addCase(fetchAppointmentById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Create
      .addCase(createAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createAppointment.fulfilled, (state, action) => {
        state.loading = false;
        state.appointments.push(action.payload);
      })
      .addCase(createAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Update
      .addCase(updateAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateAppointment.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.appointments.findIndex((a) => a.id === action.payload.id);
        if (index !== -1) {
          state.appointments[index] = action.payload;
        }
        state.currentAppointment = action.payload;
      })
      .addCase(updateAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Delete
      .addCase(deleteAppointment.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAppointment.fulfilled, (state, action) => {
        state.loading = false;
        state.appointments = state.appointments.filter((a) => a.id !== action.payload);
      })
      .addCase(deleteAppointment.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError, clearCurrentAppointment } = appointmentSlice.actions;
export default appointmentSlice.reducer;
