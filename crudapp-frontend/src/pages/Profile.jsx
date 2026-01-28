import React, { useState, useEffect, useRef } from 'react';
import {
  Container,
  Paper,
  Typography,
  TextField,
  Button,
  Box,
  Alert,
  CircularProgress,
  Grid,
  Card,
  CardContent,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  FormHelperText,
} from '@mui/material';
import {
  Person,
  Email,
  Phone,
  Home,
  Cake,
  MedicalServices,
  ArrowBack,
  PhotoCamera,
  Crop,
} from '@mui/icons-material';
import ReactCrop from 'react-image-crop';
import 'react-image-crop/dist/ReactCrop.css';
import PhoneInput from 'react-phone-input-2';
import 'react-phone-input-2/lib/material.css';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { updateProfile, uploadProfilePhoto } from '../api/authService';
import { updateUser } from '../store/slices/authSlice';

const Profile = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.auth.user);

  // Fonction pour capitaliser les noms (première lettre en majuscule)
  const capitalizeName = (name) => {
    if (!name) return '';
    return name
      .split(' ')
      .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
      .join(' ');
  };

  // Fonction pour formater le numéro de téléphone de manière lisible
  const formatPhoneNumber = (phone) => {
    if (!phone) return 'Non renseigné';

    // Ajouter le + si absent
    const phoneWithPlus = phone.startsWith('+') ? phone : `+${phone}`;

    // Formater le numéro avec des espaces pour la lisibilité
    // Exemple: +237690250170 devient +237 690 25 01 70
    const cleaned = phoneWithPlus.replace(/\s/g, '');

    // Format pour les numéros camerounais (+237)
    if (cleaned.startsWith('+237') && cleaned.length === 13) {
      return `${cleaned.slice(0, 4)} ${cleaned.slice(4, 7)} ${cleaned.slice(7, 9)} ${cleaned.slice(9, 11)} ${cleaned.slice(11)}`;
    }

    // Format pour les numéros français (+33)
    if (cleaned.startsWith('+33') && cleaned.length === 12) {
      return `${cleaned.slice(0, 3)} ${cleaned.slice(3, 4)} ${cleaned.slice(4, 6)} ${cleaned.slice(6, 8)} ${cleaned.slice(8, 10)} ${cleaned.slice(10)}`;
    }

    // Format générique: +XXX XXX XXX XXX
    const match = cleaned.match(/^(\+\d{1,3})(\d{3})(\d{3})(\d+)$/);
    if (match) {
      return `${match[1]} ${match[2]} ${match[3]} ${match[4]}`;
    }

    return phoneWithPlus;
  };

  const [formData, setFormData] = useState({
    fullName: '',
    phoneNumber: '',
    address: '',
    dateOfBirth: '',
    profilePhoto: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [cropDialogOpen, setCropDialogOpen] = useState(false);
  const [imageSrc, setImageSrc] = useState(null);
  const [crop, setCrop] = useState({
    unit: '%',
    width: 50,
    aspect: 1,
  });
  const [completedCrop, setCompletedCrop] = useState(null);
  const imgRef = useRef(null);

  useEffect(() => {
    if (user) {
      setFormData({
        fullName: user.fullName || '',
        phoneNumber: user.phoneNumber || '',
        address: user.address || '',
        dateOfBirth: user.dateOfBirth || '',
        profilePhoto: user.profilePhoto || '',
      });
      setPreviewUrl(user.profilePhoto || '');
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    // Capitaliser automatiquement le nom complet
    const finalValue = name === 'fullName' ? capitalizeName(value) : value;
    setFormData((prev) => ({
      ...prev,
      [name]: finalValue,
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Vérifier le type de fichier
      if (!file.type.startsWith('image/')) {
        setError('Veuillez sélectionner une image valide');
        return;
      }

      // Vérifier la taille (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setError('La taille de l\'image ne doit pas dépasser 5MB');
        return;
      }

      // Lire le fichier et ouvrir le dialogue de recadrage
      const reader = new FileReader();
      reader.onloadend = () => {
        setImageSrc(reader.result);
        setCropDialogOpen(true);
      };
      reader.readAsDataURL(file);
      setError('');
    }
  };

  const getCroppedImg = (image, crop) => {
    const canvas = document.createElement('canvas');
    const scaleX = image.naturalWidth / image.width;
    const scaleY = image.naturalHeight / image.height;
    canvas.width = crop.width;
    canvas.height = crop.height;
    const ctx = canvas.getContext('2d');

    ctx.drawImage(
      image,
      crop.x * scaleX,
      crop.y * scaleY,
      crop.width * scaleX,
      crop.height * scaleY,
      0,
      0,
      crop.width,
      crop.height
    );

    return new Promise((resolve) => {
      canvas.toBlob((blob) => {
        if (!blob) {
          console.error('Canvas is empty');
          return;
        }
        blob.name = 'cropped.jpg';
        resolve(blob);
      }, 'image/jpeg', 0.95);
    });
  };

  const handleCropComplete = async () => {
    if (completedCrop && imgRef.current) {
      const croppedBlob = await getCroppedImg(imgRef.current, completedCrop);
      const croppedFile = new File([croppedBlob], 'profile-photo.jpg', { type: 'image/jpeg' });

      setSelectedFile(croppedFile);

      // Créer une URL de prévisualisation
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result);
      };
      reader.readAsDataURL(croppedFile);

      setCropDialogOpen(false);
      setImageSrc(null);
    }
  };

  const handleCropCancel = () => {
    setCropDialogOpen(false);
    setImageSrc(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      let photoUrl = formData.profilePhoto;

      // Si un fichier a été sélectionné, l'uploader d'abord
      if (selectedFile) {
        const uploadResponse = await uploadProfilePhoto(selectedFile);
        photoUrl = uploadResponse.url;
      }

      // Mettre à jour le profil avec l'URL de la photo
      const updatedData = {
        ...formData,
        profilePhoto: photoUrl,
      };

      const updatedUser = await updateProfile(updatedData);
      dispatch(updateUser(updatedUser));
      setSuccess('Profil mis à jour avec succès !');
      setSelectedFile(null);

      // Rediriger vers le tableau de bord après 1.5 secondes
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (err) {
      console.error('Erreur lors de la mise à jour du profil:', err);
      setError(err.response?.data?.message || 'Erreur lors de la mise à jour du profil');
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <CircularProgress />
      </Container>
    );
  }

  const isDoctor = user.userType === 'DOCTOR' || user.userType === 'Médecin' || user.role === 'DOCTOR';

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#BBDEFB', py: 4 }}>
      <Container maxWidth="lg">
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
          <Button
            startIcon={<ArrowBack />}
            onClick={() => navigate('/dashboard')}
            sx={{ mr: 2 }}
          >
            Retour au tableau de bord
          </Button>
        </Box>

      <Typography variant="h4" gutterBottom sx={{ mb: 4, fontWeight: 'bold', color: 'primary.main' }}>
        Mon Profil
      </Typography>

      {/* Section Mes Informations */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
          Mes Informations
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card sx={{
              bgcolor: 'rgba(255, 255, 255, 0.3)',
              backdropFilter: 'blur(3px)',
              boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.05)',
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Person sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Nom complet
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {capitalizeName(user.fullName) || 'Non renseigné'}
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Email sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Email
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {user.email || 'Non renseigné'}
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Phone sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Téléphone
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {formatPhoneNumber(user.phoneNumber)}
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Home sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Adresse
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {user.address || 'Non renseigné'}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Card sx={{
              bgcolor: 'rgba(255, 255, 255, 0.3)',
              backdropFilter: 'blur(3px)',
              boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.05)',
            }}>
              <CardContent>
                {isDoctor && (
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <MedicalServices sx={{ mr: 2, color: 'primary.main' }} />
                    <Box>
                      <Typography variant="caption" color="text.secondary">
                        Spécialité
                      </Typography>
                      <Typography variant="body1" fontWeight="bold">
                        {user.specialty || 'Non renseigné'}
                      </Typography>
                    </Box>
                  </Box>
                )}
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Cake sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Date de naissance
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {user.dateOfBirth || 'Non renseigné'}
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Person sx={{ mr: 2, color: 'primary.main' }} />
                  <Box>
                    <Typography variant="caption" color="text.secondary">
                      Nom d'utilisateur
                    </Typography>
                    <Typography variant="body1" fontWeight="bold">
                      {user.username}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Box>

      <Divider sx={{ my: 4 }} />

      {/* Section Modifier mes informations */}
      <Paper elevation={3} sx={{
        p: 4,
        bgcolor: 'rgba(255, 255, 255, 0.3)',
        backdropFilter: 'blur(3px)',
        boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.05)',
      }}>
        <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
          Modifier mes informations
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {success && (
          <Alert severity="success" sx={{ mb: 2 }}>
            {success}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Nom complet"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <PhoneInput
                value={formData.phoneNumber}
                onChange={(value) => setFormData((prev) => ({ ...prev, phoneNumber: value }))}
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
            </Grid>

            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Adresse"
                name="address"
                value={formData.address}
                onChange={handleChange}
                multiline
                rows={2}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Date de naissance"
                name="dateOfBirth"
                type="date"
                value={formData.dateOfBirth}
                onChange={handleChange}
                InputLabelProps={{
                  shrink: true,
                }}
              />
            </Grid>

            {isDoctor && (
              <Grid item xs={12}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                    Photo de profil
                  </Typography>

                  {previewUrl && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                      <Box
                        component="img"
                        src={previewUrl}
                        alt="Aperçu de la photo de profil"
                        sx={{
                          width: 150,
                          height: 150,
                          borderRadius: '50%',
                          objectFit: 'cover',
                          border: '3px solid',
                          borderColor: 'primary.main',
                        }}
                      />
                    </Box>
                  )}

                  <Button
                    variant="outlined"
                    component="label"
                    startIcon={<PhotoCamera />}
                    fullWidth
                  >
                    {selectedFile ? 'Changer la photo' : 'Choisir une photo'}
                    <input
                      type="file"
                      hidden
                      accept="image/*"
                      onChange={handleFileChange}
                    />
                  </Button>

                  {selectedFile && (
                    <Typography variant="body2" color="text.secondary">
                      Fichier sélectionné: {selectedFile.name}
                    </Typography>
                  )}
                </Box>
              </Grid>
            )}

            <Grid item xs={12}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                size="large"
                disabled={loading}
                sx={{ mt: 2 }}
              >
                {loading ? <CircularProgress size={24} /> : 'Mettre à jour mes informations'}
              </Button>
            </Grid>
          </Grid>
        </Box>
      </Paper>

      {/* Dialog de recadrage */}
      <Dialog
        open={cropDialogOpen}
        onClose={handleCropCancel}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Crop />
            <Typography variant="h6">Recadrer votre photo de profil</Typography>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
            {imageSrc && (
              <ReactCrop
                crop={crop}
                onChange={(c) => setCrop(c)}
                onComplete={(c) => setCompletedCrop(c)}
                aspect={1}
                circularCrop
              >
                <img
                  ref={imgRef}
                  src={imageSrc}
                  alt="Image à recadrer"
                  style={{ maxWidth: '100%', maxHeight: '500px' }}
                />
              </ReactCrop>
            )}
          </Box>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 2, textAlign: 'center' }}>
            Déplacez et redimensionnez le cercle pour centrer votre photo
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCropCancel} color="inherit">
            Annuler
          </Button>
          <Button
            onClick={handleCropComplete}
            variant="contained"
            disabled={!completedCrop}
          >
            Valider
          </Button>
        </DialogActions>
      </Dialog>
      </Container>
    </Box>
  );
};

export default Profile;
