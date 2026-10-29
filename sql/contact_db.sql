-- ============================================================
--  Contact Management System — MySQL Setup Script
--  Run once before starting the application.
-- ============================================================

-- 1. Create the database
CREATE DATABASE IF NOT EXISTS contact_db;

-- 2. Switch to it
USE contact_db;

-- 3. Create the contacts table
CREATE TABLE IF NOT EXISTS contacts (
    id    INT          NOT NULL AUTO_INCREMENT,
    name  VARCHAR(100) NOT NULL,
    phone VARCHAR(20)  NOT NULL,
    email VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

-- 4. Optional: insert a couple of sample rows to verify things work
INSERT INTO contacts (name, phone, email) VALUES
    ('Alice Johnson', '9876543210', 'alice@example.com'),
    ('Bob Smith',     '8765432109', 'bob@example.com');
