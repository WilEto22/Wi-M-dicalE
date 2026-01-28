-- Migration pour ajouter des contraintes d'unicité sur les numéros de téléphone
-- Ce script doit être exécuté sur une base de données existante

-- Supprimer les doublons potentiels avant d'ajouter les contraintes
-- Pour la table users
DELETE u1 FROM users u1
INNER JOIN users u2
WHERE u1.id > u2.id
AND (u1.phone_number = u2.phone_number OR u1.email = u2.email);

-- Pour la table patients
DELETE p1 FROM patients p1
INNER JOIN patients p2
WHERE p1.id > p2.id
AND (p1.phone_number = p2.phone_number OR p1.email = p2.email);

-- Ajouter les contraintes d'unicité sur la table users
ALTER TABLE users
ADD UNIQUE INDEX idx_users_phone_number (phone_number);

-- Ajouter les contraintes d'unicité sur la table patients
ALTER TABLE patients
ADD UNIQUE INDEX idx_patients_phone_number (phone_number);

-- Vérifier les contraintes
SHOW INDEX FROM users WHERE Key_name = 'idx_users_phone_number';
SHOW INDEX FROM patients WHERE Key_name = 'idx_patients_phone_number';
