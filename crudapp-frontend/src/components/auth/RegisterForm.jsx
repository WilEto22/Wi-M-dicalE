import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { useForm, useWatch, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  TextField,
  Button,
  CircularProgress,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from '@mui/material';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/material.css';
import { register as registerUser } from '../../store/slices/authSlice';

const schema = yup.object({
  username: yup.string().required('Le nom d\'utilisateur est requis'),
  email: yup.string().email('Email invalide').required('L\'email est requis'),
  password: yup
    .string()
    .min(6, 'Le mot de passe doit contenir au moins 6 caractères')
    .required('Le mot de passe est requis'),
  confirmPassword: yup
    .string()
    .oneOf([yup.ref('password'), null], 'Les mots de passe ne correspondent pas')
    .required('Veuillez confirmer le mot de passe'),
  userType: yup.string().required('Le type d\'utilisateur est requis'),
  // Champ fullName requis pour tous les utilisateurs
  fullName: yup.string().required('Le nom complet est requis'),
  phoneNumber: yup.string().required('Le numéro de téléphone est requis'),
  dateOfBirth: yup.string().when('userType', {
    is: 'PATIENT',
    then: (schema) => schema.required('La date de naissance est requise pour les patients'),
    otherwise: (schema) => schema.notRequired(),
  }),
  // Champs conditionnels pour DOCTOR
  medicalSpecialty: yup.string().when('userType', {
    is: 'DOCTOR',
    then: (schema) => schema.required('La spécialité est requise pour les médecins'),
    otherwise: (schema) => schema.notRequired(),
  }),
  address: yup.string().when('userType', {
    is: 'DOCTOR',
    then: (schema) => schema.required('L\'adresse est requise pour les médecins'),
    otherwise: (schema) => schema.notRequired(),
  }),
}, [['userType', 'fullName'], ['userType', 'dateOfBirth'], ['userType', 'medicalSpecialty'], ['userType', 'address']]);

const RegisterForm = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [userEmail, setUserEmail] = useState('');

  // Fonction pour capitaliser les noms (première lettre en majuscule)
  const capitalizeName = (name) => {
    if (!name) return '';
    return name
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
  };

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
    defaultValues: {
      userType: 'PATIENT',
    },
  });

  // Observer le type d'utilisateur pour afficher les champs conditionnels
  const userType = useWatch({
    control,
    name: 'userType',
    defaultValue: 'PATIENT',
  });

  const onSubmit = async (data) => {
    try {
      setLoading(true);
      setError('');
      setSuccess(false);
      const { confirmPassword, ...registerData } = data;

      // Le backend attend "medicalSpecialty" dans le JSON (grâce à @JsonProperty)
      // Pas besoin de mapper, on garde medicalSpecialty tel quel

      console.log('📤 Données envoyées au backend:', registerData);

      await dispatch(registerUser(registerData)).unwrap();

      // Afficher le message de succès
      setUserEmail(registerData.email);
      setSuccess(true);

      // Rediriger vers la page de connexion après 5 secondes
      setTimeout(() => {
        navigate('/login');
      }, 5000);
    } catch (err) {
      // err contient déjà le message formaté depuis authSlice
      console.error('Erreur d\'inscription:', err);
      const errorMessage = typeof err === 'string' ? err : (err?.message || 'Erreur lors de l\'inscription');
      setError(errorMessage);
      // Faire défiler vers le haut pour voir l'erreur
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          <strong>✅ Inscription réussie !</strong>
          <br />
          Un email de confirmation a été envoyé à <strong>{userEmail}</strong>.
          <br />
          Veuillez vérifier votre boîte de réception (et vos spams).
          <br />
          <em>Vous serez redirigé vers la page de connexion dans 5 secondes...</em>
        </Alert>
      )}

      <TextField
        margin="normal"
        required
        fullWidth
        id="username"
        label="Nom d'utilisateur"
        name="username"
        autoComplete="username"
        autoFocus
        {...register('username')}
        error={!!errors.username}
        helperText={errors.username?.message}
      />

      <TextField
        margin="normal"
        required
        fullWidth
        id="email"
        label="Email"
        name="email"
        autoComplete="email"
        {...register('email')}
        error={!!errors.email}
        helperText={errors.email?.message}
      />

      <FormControl fullWidth margin="normal" error={!!errors.userType}>
        <InputLabel id="userType-label">Type d'utilisateur</InputLabel>
        <Select
          labelId="userType-label"
          id="userType"
          label="Type d'utilisateur"
          defaultValue="PATIENT"
          {...register('userType')}
        >
          <MenuItem value="PATIENT">Patient</MenuItem>
          <MenuItem value="DOCTOR">Médecin</MenuItem>
        </Select>
        {errors.userType && (
          <FormHelperText>{errors.userType?.message}</FormHelperText>
        )}
      </FormControl>

      {/* Champ Nom complet - pour tous les utilisateurs */}
      <TextField
        margin="normal"
        required
        fullWidth
        id="fullName"
        label="Nom complet"
        name="fullName"
        autoComplete="name"
        {...register('fullName')}
        onChange={(e) => {
          e.target.value = capitalizeName(e.target.value);
          register('fullName').onChange(e);
        }}
        error={!!errors.fullName}
        helperText={errors.fullName?.message}
      />

      {/* Champ téléphone - pour tous les utilisateurs */}
      <Controller
        name="phoneNumber"
        control={control}
        render={({ field }) => (
          <Box sx={{ mt: 2, mb: 1 }}>
            <PhoneInput
              {...field}
              country={'cm'}
              enableSearch={true}
              searchPlaceholder="Rechercher un pays..."
              containerStyle={{ width: '100%' }}
              inputStyle={{
                width: '100%',
                height: '56px',
                fontSize: '16px',
              }}
              buttonStyle={{
                borderRadius: '4px 0 0 4px',
              }}
              dropdownStyle={{
                maxHeight: '200px',
              }}
            />
            {errors.phoneNumber && (
              <FormHelperText error sx={{ ml: 2 }}>
                {errors.phoneNumber?.message}
              </FormHelperText>
            )}
          </Box>
        )}
      />

      {/* Champs conditionnels pour PATIENT */}
      {userType === 'PATIENT' && (
        <>
          <TextField
            margin="normal"
            required
            fullWidth
            id="dateOfBirth"
            label="Date de naissance"
            name="dateOfBirth"
            type="date"
            InputLabelProps={{ shrink: true }}
            {...register('dateOfBirth')}
            error={!!errors.dateOfBirth}
            helperText={errors.dateOfBirth?.message}
          />
        </>
      )}

      {/* Champs conditionnels pour DOCTOR */}
      {userType === 'DOCTOR' && (
        <>
          <TextField
            margin="normal"
            required
            fullWidth
            id="address"
            label="Adresse"
            name="address"
            autoComplete="street-address"
            {...register('address')}
            error={!!errors.address}
            helperText={errors.address?.message}
          />

          <FormControl fullWidth margin="normal" error={!!errors.medicalSpecialty}>
            <InputLabel id="medicalSpecialty-label">Spécialité médicale</InputLabel>
            <Select
              labelId="medicalSpecialty-label"
              id="medicalSpecialty"
              label="Spécialité médicale"
              {...register('medicalSpecialty')}
            >
            <MenuItem value="ALLERGOLOGIE">Allergologie</MenuItem>
            <MenuItem value="ANATOMOPATHOLOGIE">Anatomopathologie</MenuItem>
            <MenuItem value="ANESTHESIE_REANIMATION">Anesthésie-réanimation</MenuItem>
            <MenuItem value="AUDIOLOGIE">Audiologie</MenuItem>
            <MenuItem value="BIOLOGIE_MEDICALE">Biologie médicale</MenuItem>
            <MenuItem value="CARDIOLOGIE">Cardiologie</MenuItem>
            <MenuItem value="CHIRURGIE_CARDIAQUE">Chirurgie cardiaque</MenuItem>
            <MenuItem value="CHIRURGIE_GENERALE">Chirurgie générale</MenuItem>
            <MenuItem value="CHIRURGIE_ORTHOPEDIQUE">Chirurgie orthopédique</MenuItem>
            <MenuItem value="CHIRURGIE_PEDIATRIQUE">Chirurgie pédiatrique</MenuItem>
            <MenuItem value="CHIRURGIE_PLASTIQUE">Chirurgie plastique, reconstructrice et esthétique</MenuItem>
            <MenuItem value="CHIRURGIE_VISCERALE">Chirurgie viscérale</MenuItem>
            <MenuItem value="DERMATOLOGIE">Dermatologie</MenuItem>
            <MenuItem value="ENDOCRINOLOGIE">Endocrinologie</MenuItem>
            <MenuItem value="GASTRO_ENTEROLOGIE">Gastro-entérologie</MenuItem>
            <MenuItem value="GERIATRIE">Gériatrie</MenuItem>
            <MenuItem value="HEMATOLOGIE">Hématologie</MenuItem>
            <MenuItem value="INFECTIOLOGIE">Infectiologie</MenuItem>
            <MenuItem value="MEDECINE_GENERALE">Médecine générale</MenuItem>
            <MenuItem value="MEDECINE_LEGALE">Médecine légale</MenuItem>
            <MenuItem value="MEDECINE_NUCLEAIRE">Médecine nucléaire</MenuItem>
            <MenuItem value="MEDECINE_PHYSIQUE_READAPTATION">Médecine physique et de réadaptation</MenuItem>
            <MenuItem value="MEDECINE_SPORT">Médecine du sport</MenuItem>
            <MenuItem value="MEDECINE_TRAVAIL">Médecine du travail</MenuItem>
            <MenuItem value="MEDECINE_URGENCE">Médecine d'urgence</MenuItem>
            <MenuItem value="NEPHROLOGIE">Néphrologie</MenuItem>
            <MenuItem value="NEUROCHIRURGIE">Neurochirurgie</MenuItem>
            <MenuItem value="NEUROLOGIE">Neurologie</MenuItem>
            <MenuItem value="ONCOLOGIE">Oncologie</MenuItem>
            <MenuItem value="OPHTALMOLOGIE">Ophtalmologie</MenuItem>
            <MenuItem value="ORL">ORL (Oto-rhino-laryngologie)</MenuItem>
            <MenuItem value="PEDIATRIE">Pédiatrie</MenuItem>
            <MenuItem value="PNEUMOLOGIE">Pneumologie</MenuItem>
            <MenuItem value="PSYCHIATRIE">Psychiatrie</MenuItem>
            <MenuItem value="RADIOLOGIE">Radiologie</MenuItem>
            <MenuItem value="RHUMATOLOGIE">Rhumatologie</MenuItem>
            <MenuItem value="SANTE_PUBLIQUE">Santé publique</MenuItem>
          </Select>
          {errors.medicalSpecialty && (
            <FormHelperText>{errors.medicalSpecialty?.message}</FormHelperText>
          )}
        </FormControl>
        </>
      )}

      <TextField
        margin="normal"
        required
        fullWidth
        name="password"
        label="Mot de passe"
        type="password"
        id="password"
        autoComplete="new-password"
        {...register('password')}
        error={!!errors.password}
        helperText={errors.password?.message}
      />

      <TextField
        margin="normal"
        required
        fullWidth
        name="confirmPassword"
        label="Confirmer le mot de passe"
        type="password"
        id="confirmPassword"
        autoComplete="new-password"
        {...register('confirmPassword')}
        error={!!errors.confirmPassword}
        helperText={errors.confirmPassword?.message}
      />

      <Button
        type="submit"
        fullWidth
        variant="contained"
        sx={{ mt: 3, mb: 2 }}
        disabled={loading || success}
      >
        {loading ? <CircularProgress size={24} /> : success ? '✅ Inscription réussie' : 'S\'inscrire'}
      </Button>
    </Box>
  );
};

export default RegisterForm;
