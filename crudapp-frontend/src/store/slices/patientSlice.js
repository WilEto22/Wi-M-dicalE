import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import * as patientService from '../../api/patientService';

// Thunks
export const fetchPatients = createAsyncThunk(
  'patients/fetchAll',
  async ({ page = 0, size = 10 }, { rejectWithValue }) => {
    try {
      const response = await patientService.getAllPatients(page, size);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la récupération des patients');
    }
  }
);

export const fetchPatientById = createAsyncThunk(
  'patients/fetchById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await patientService.getPatientById(id);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Patient non trouvé');
    }
  }
);

export const createPatient = createAsyncThunk(
  'patients/create',
  async (patientData, { rejectWithValue }) => {
    try {
      const response = await patientService.createPatient(patientData);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la création du patient');
    }
  }
);

export const updatePatient = createAsyncThunk(
  'patients/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      const response = await patientService.updatePatient(id, data);
      return response;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la mise à jour du patient');
    }
  }
);

export const deletePatient = createAsyncThunk(
  'patients/delete',
  async (id, { rejectWithValue }) => {
    try {
      await patientService.deletePatient(id);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Erreur lors de la suppression du patient');
    }
  }
);

// Slice
const patientSlice = createSlice({
  name: 'patients',
  initialState: {
    patients: [],
    currentPatient: null,
    totalPages: 0,
    totalElements: 0,
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentPatient: (state) => {
      state.currentPatient = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch all
      .addCase(fetchPatients.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPatients.fulfilled, (state, action) => {
        state.loading = false;
        state.patients = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.totalElements = action.payload.totalElements || 0;
      })
      .addCase(fetchPatients.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Fetch by ID
      .addCase(fetchPatientById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchPatientById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentPatient = action.payload;
      })
      .addCase(fetchPatientById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Create
      .addCase(createPatient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createPatient.fulfilled, (state, action) => {
        state.loading = false;
        state.patients.push(action.payload);
      })
      .addCase(createPatient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Update
      .addCase(updatePatient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updatePatient.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.patients.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.patients[index] = action.payload;
        }
        state.currentPatient = action.payload;
      })
      .addCase(updatePatient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Delete
      .addCase(deletePatient.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deletePatient.fulfilled, (state, action) => {
        state.loading = false;
        state.patients = state.patients.filter((p) => p.id !== action.payload);
      })
      .addCase(deletePatient.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { clearError, clearCurrentPatient } = patientSlice.actions;
export default patientSlice.reducer;
