-- Insert initial admin user
INSERT INTO users (username, password, role, name, email) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'Admin User', 'admin@smcem.com');
-- Password is 'password' encoded with BCrypt