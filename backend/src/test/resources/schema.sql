-- Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    profile_picture VARCHAR(255),
    bio TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- User-Role Junction Table
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO roles (name, description) VALUES ('ROLE_PROD', 'Can upload and download audio samples');
INSERT INTO roles (name, description) VALUES ('ROLE_MOD', 'Can moderate content and users');
INSERT INTO roles (name, description) VALUES ('ROLE_ADMIN', 'Has full administrative privileges');