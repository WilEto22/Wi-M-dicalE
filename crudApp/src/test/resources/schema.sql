-- Schema for H2 test database
-- This file is automatically executed by Spring Boot when spring.sql.init.mode=always

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    phone_number VARCHAR(20) UNIQUE,
    address VARCHAR(255),
    date_of_birth DATE,
    profile_photo VARCHAR(500),
    roles VARCHAR(255),
    user_type VARCHAR(50),
    medical_specialty VARCHAR(100),
    provider VARCHAR(50),
    provider_id VARCHAR(100),
    email_verified BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone_number VARCHAR(20) UNIQUE,
    age INT,
    address VARCHAR(255),
    blood_type VARCHAR(10),
    allergies TEXT,
    chronic_diseases TEXT,
    medications TEXT,
    medical_history TEXT,
    emergency_contact VARCHAR(100),
    emergency_phone VARCHAR(20),
    insurance_number VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    last_visit DATE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(50),
    notes TEXT,
    patient_id BIGINT,
    doctor_id BIGINT,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS doctor_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    doctor_id BIGINT NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS doctor_availability_exceptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exception_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    is_available BOOLEAN DEFAULT FALSE,
    reason VARCHAR(255),
    doctor_id BIGINT NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES users(id)
);
