-- ================================
-- CrudApp Medical - MySQL Initialization Script
-- ================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS crudapp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE crudapp_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON crudapp_db.* TO 'crudapp_user'@'%';
FLUSH PRIVILEGES;

-- Log initialization
SELECT 'Database crudapp_db initialized successfully' AS message;
