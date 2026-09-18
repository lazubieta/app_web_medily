-- Copia de seguridad logica de Medily.
-- Compatible con MySQL 8. Ajustar AUTO_INCREMENT si ya existen datos.
CREATE DATABASE IF NOT EXISTS medily CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medily;

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS devices (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  brand VARCHAR(255) NOT NULL,
  price DECIMAL(15,2) NOT NULL,
  image VARCHAR(1000),
  description VARCHAR(2000),
  release_date DATE,
  category VARCHAR(255) NOT NULL
  ,CONSTRAINT fk_devices_category FOREIGN KEY (category) REFERENCES categories(name)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS device_features (
  device_id BIGINT NOT NULL,
  feature_name VARCHAR(255) NOT NULL,
  feature_value VARCHAR(500),
  PRIMARY KEY (device_id, feature_name),
  CONSTRAINT fk_device_features_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS admin_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL
);

INSERT INTO categories (name, description) VALUES
('Gama alta', 'Dispositivos premium con especificaciones avanzadas'),
('Gama media', 'Equipos equilibrados para uso diario');

INSERT INTO devices (name, brand, price, image, description, release_date, category) VALUES
('iPhone 15 Pro', 'Apple', 4899000, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=900&q=80', 'Smartphone premium con chasis de titanio, alto rendimiento y sistema de camaras avanzado.', '2023-09-22', 'Gama alta'),
('Samsung Galaxy S24 Ultra', 'Samsung', 5399000, 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?auto=format&fit=crop&w=900&q=80', 'Dispositivo Android de alto desempeno con pantalla amplia, S Pen y camaras de largo alcance.', '2024-01-31', 'Gama alta'),
('Xiaomi Redmi Note 13 Pro', 'Xiaomi', 1499000, 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80', 'Equipo equilibrado para uso diario, fotografia nitida y carga rapida.', '2024-01-15', 'Gama media'),
('Motorola Edge 50 Pro', 'Motorola', 2599000, 'https://images.unsplash.com/photo-1580910051074-3eb694886505?auto=format&fit=crop&w=900&q=80', 'Telefono delgado con pantalla curva, carga veloz y experiencia Android limpia.', '2024-04-03', 'Gama media'),
('Google Pixel 8', 'Google', 3199000, 'https://images.unsplash.com/photo-1598965402089-897ce52e8355?auto=format&fit=crop&w=900&q=80', 'Smartphone compacto enfocado en fotografia computacional y funciones inteligentes.', '2023-10-12', 'Gama alta'),
('Honor Magic6 Lite', 'Honor', 1299000, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80', 'Movil de gran autonomia con pantalla AMOLED y diseno ligero para uso cotidiano.', '2024-01-10', 'Gama media');

INSERT INTO device_features (device_id, feature_name, feature_value) VALUES
(1, 'display', '6.1 pulgadas Super Retina XDR'),
(1, 'camera', 'Triple camara de 48 MP'),
(1, 'storage', '128 GB'),
(2, 'display', '6.8 pulgadas Dynamic AMOLED 2X'),
(2, 'camera', 'Camara principal de 200 MP'),
(2, 'storage', '256 GB'),
(3, 'display', '6.67 pulgadas AMOLED'),
(3, 'camera', 'Camara principal de 200 MP'),
(3, 'storage', '256 GB'),
(4, 'display', '6.7 pulgadas pOLED'),
(4, 'camera', 'Triple camara de 50 MP'),
(4, 'storage', '512 GB'),
(5, 'display', '6.2 pulgadas OLED'),
(5, 'camera', 'Doble camara de 50 MP'),
(5, 'storage', '128 GB'),
(6, 'display', '6.78 pulgadas AMOLED'),
(6, 'camera', 'Camara principal de 108 MP'),
(6, 'storage', '256 GB');

-- Contraseña de demo: Medily2026*.
INSERT INTO admin_users (username, email, password_hash) VALUES
('admin', 'admin@medily.com', '$2a$10$Eg4lXlO.ljlY91Xwd.GAqeRk4shjjWlj79RHGwei2ddlIKsxCv.JO');
