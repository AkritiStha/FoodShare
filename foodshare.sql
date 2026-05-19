-- ============================================================
-- FoodShare Database Schema
-- CS5054NT Coursework
-- MySQL 8.0 Compatible
-- ============================================================

CREATE DATABASE IF NOT EXISTS foodshare
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE foodshare;

-- ============================================================
-- TABLE: users
-- Stores donors, NGOs, and admins
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
                                     id          INT AUTO_INCREMENT PRIMARY KEY,
                                     name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,             -- BCrypt hash
    role        ENUM('donor','ngo','admin') NOT NULL DEFAULT 'donor',
    phone       VARCHAR(20),
    address     VARCHAR(255),
    approved    TINYINT(1)    NOT NULL DEFAULT 0,   -- 0=pending, 1=approved (NGO only)
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role  (role)
    ) ENGINE=InnoDB;

-- ============================================================
-- TABLE: food_items
-- Food listings created by donors
-- ============================================================
CREATE TABLE IF NOT EXISTS food_items (
                                          id               INT AUTO_INCREMENT PRIMARY KEY,
                                          donor_id         INT           NOT NULL,
                                          name             VARCHAR(150)  NOT NULL,
    quantity         DECIMAL(10,2) NOT NULL,           -- in kg or units
    quantity_unit    VARCHAR(20)   NOT NULL DEFAULT 'kg',
    description      TEXT,
    expiry_date      DATETIME      NOT NULL,
    pickup_location  VARCHAR(255)  NOT NULL,
    latitude         DECIMAL(10,7) NOT NULL DEFAULT 0.0,
    longitude        DECIMAL(10,7) NOT NULL DEFAULT 0.0,
    status           ENUM('available','requested','completed','expired') NOT NULL DEFAULT 'available',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_food_donor    (donor_id),
    INDEX idx_food_expiry   (expiry_date),
    INDEX idx_food_status   (status),
    INDEX idx_food_location (latitude, longitude)
    ) ENGINE=InnoDB;

-- ============================================================
-- TABLE: requests
-- NGO requests for food items
-- ============================================================
CREATE TABLE IF NOT EXISTS requests (
                                        id           INT AUTO_INCREMENT PRIMARY KEY,
                                        food_item_id INT  NOT NULL,
                                        ngo_id       INT  NOT NULL,
                                        donor_id     INT  NOT NULL,
                                        status       ENUM('PENDING','ACCEPTED','COMPLETED','REJECTED','EXPIRED') NOT NULL DEFAULT 'PENDING',
    message      TEXT,                                -- optional note from NGO
    rating       TINYINT,                             -- 1-5 star rating by NGO after completion
    rating_note  VARCHAR(255),
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE CASCADE,
    FOREIGN KEY (ngo_id)       REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (donor_id)     REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_req_food   (food_item_id),
    INDEX idx_req_ngo    (ngo_id),
    INDEX idx_req_donor  (donor_id),
    INDEX idx_req_status (status)
    ) ENGINE=InnoDB;

-- ============================================================
-- TABLE: notifications
-- In-app notifications for donors and NGOs
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
                                             id          INT AUTO_INCREMENT PRIMARY KEY,
                                             user_id     INT          NOT NULL,
                                             message     VARCHAR(500) NOT NULL,
    is_read     TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_read (is_read)
    ) ENGINE=InnoDB;

-- ============================================================
-- TABLE: pickup_schedules
-- Agreed pickup times after donor accepts request
-- ============================================================
CREATE TABLE IF NOT EXISTS pickup_schedules (
                                                id          INT AUTO_INCREMENT PRIMARY KEY,
                                                request_id  INT      NOT NULL UNIQUE,
                                                pickup_time DATETIME NOT NULL,
                                                notes       VARCHAR(255),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE,
    INDEX idx_sched_request (request_id),
    INDEX idx_sched_time    (pickup_time)
    ) ENGINE=InnoDB;

-- ============================================================
-- SAMPLE DATA
-- Passwords are BCrypt hash of "Password1!" (salt=10)
-- ============================================================

-- Admin user (pre-approved)
INSERT INTO users (name, email, password, role, phone, address, approved) VALUES
    ('Admin User',
     'admin@foodshare.com',
     '$2a$10$pQ5fXkE1SPu/O3e3L/bE7ekp82NVP2JrVJeOO/Wrp/eJNwt0eHLSu',
     'admin', '0000000000', 'FoodShare HQ', 1);

-- Donors
INSERT INTO users (name, email, password, role, phone, address, approved) VALUES
                                                                              ('Green Leaf Restaurant',
                                                                               'greenleaf@donor.com',
                                                                               '$2a$10$pQ5fXkE1SPu/O3e3L/bE7ekp82NVP2JrVJeOO/Wrp/eJNwt0eHLSu',
                                                                               'donor', '07700111222', '12 High Street, London', 1),
                                                                              ('City Hotel Kitchen',
                                                                               'cityhotel@donor.com',
                                                                               '$2a$10$pQ5fXkE1SPu/O3e3L/bE7ekp82NVP2JrVJeOO/Wrp/eJNwt0eHLSu',
                                                                               'donor', '07700333444', '45 Park Avenue, London', 1);

-- NGOs (require admin approval)
INSERT INTO users (name, email, password, role, phone, address, approved) VALUES
                                                                              ('Hope Shelter NGO',
                                                                               'hope@ngo.com',
                                                                               '$2a$10$pQ5fXkE1SPu/O3e3L/bE7ekp82NVP2JrVJeOO/Wrp/eJNwt0eHLSu',
                                                                               'ngo', '07700555666', '8 Shelter Road, London', 1),
                                                                              ('Community Kitchen',
                                                                               'community@ngo.com',
                                                                               '$2a$10$pQ5fXkE1SPu/O3e3L/bE7ekp82NVP2JrVJeOO/Wrp/eJNwt0eHLSu',
                                                                               'ngo', '07700777888', '22 Community Lane, London', 1);


-- Food items from donors
INSERT INTO food_items (donor_id, name, quantity, quantity_unit, description, expiry_date, pickup_location, latitude, longitude, status) VALUES
                                                                                                                                             (2, 'Cooked Rice',     15.00, 'kg',    'Freshly cooked basmati rice, ideal for distribution', DATE_ADD(NOW(), INTERVAL 12 HOUR), '12 High Street, London', 51.5074, -0.1278, 'available'),
                                                                                                                                             (2, 'Vegetable Curry', 10.00, 'kg',    'Mixed vegetable curry, mildly spiced',                DATE_ADD(NOW(), INTERVAL 24 HOUR), '12 High Street, London', 51.5074, -0.1278, 'available'),
                                                                                                                                             (3, 'Bread Loaves',    30.00, 'units', 'Assorted whole-grain bread loaves',                   DATE_ADD(NOW(), INTERVAL 6  HOUR), '45 Park Avenue, London', 51.5155, -0.0922, 'available'),
                                                                                                                                             (3, 'Fruit Salad',      8.00, 'kg',    'Fresh fruit salad — apples, grapes, oranges',         DATE_ADD(NOW(), INTERVAL 18 HOUR), '45 Park Avenue, London', 51.5155, -0.0922, 'requested'),
                                                                                                                                             (2, 'Pasta Bolognese', 12.00, 'kg',    'Beef pasta bolognese, prepared today',                DATE_ADD(NOW(), INTERVAL 36 HOUR), '12 High Street, London', 51.5074, -0.1278, 'available');

-- Requests
INSERT INTO requests (food_item_id, ngo_id, donor_id, status, message) VALUES
                                                                           (4, 4, 3, 'ACCEPTED',  'We can pick up today afternoon.'),
                                                                           (1, 5, 2, 'PENDING',   'We need this urgently for 50 people tonight.'),
                                                                           (2, 4, 2, 'COMPLETED', 'Thank you! Food was excellent quality.');

-- Pickup schedule for the accepted request
INSERT INTO pickup_schedules (request_id, pickup_time, notes) VALUES
    (1, DATE_ADD(NOW(), INTERVAL 4 HOUR), 'Please bring own containers');

-- Notifications
INSERT INTO notifications (user_id, message, is_read) VALUES
                                                          (2, 'Community Kitchen has requested your Pasta Bolognese listing.',           0),
                                                          (4, 'Your request for Fruit Salad has been ACCEPTED by City Hotel Kitchen.',   0),
                                                          (2, 'Hope Shelter NGO has completed pickup for Vegetable Curry. Thank you!',   1),
                                                          (5, 'Your request for Cooked Rice is PENDING donor approval.',                 0);
