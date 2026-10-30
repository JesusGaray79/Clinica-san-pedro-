-- =====================================================================
-- Sistema informático para el registro de citas médicas
-- Clínica San Pedro - Sede Los Olivos
-- Ejecutar completo en MySQL Workbench (rayo, o Ctrl+Shift+Enter).
-- OJO: al volver a ejecutarlo se reinician las tablas y los datos de ejemplo.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS clinica_san_pedro
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica_san_pedro;

DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS medico;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS paciente;
DROP TABLE IF EXISTS especialidad;

CREATE TABLE especialidad (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE paciente (
    dni      CHAR(8) PRIMARY KEY,
    nombres  VARCHAR(80) NOT NULL,
    edad     INT NOT NULL,
    telefono VARCHAR(9) NOT NULL DEFAULT '',
    sexo     CHAR(1) NOT NULL CHECK (sexo IN ('M','F'))
);

CREATE TABLE medico (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    dni             CHAR(8) NOT NULL UNIQUE,
    nombres         VARCHAR(80) NOT NULL,
    telefono        VARCHAR(9) NOT NULL DEFAULT '',
    id_especialidad INT NOT NULL,
    tarifa          DECIMAL(8,2) NOT NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (id_especialidad) REFERENCES especialidad(id)
);

CREATE TABLE usuario (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    dni      CHAR(8) NOT NULL UNIQUE,
    nombres  VARCHAR(80) NOT NULL,
    telefono VARCHAR(9) NOT NULL DEFAULT '',
    username VARCHAR(20) NOT NULL UNIQUE,
    clave    VARCHAR(64) NOT NULL,          -- hash SHA-256
    rol      VARCHAR(15) NOT NULL CHECK (rol IN ('ADMINISTRADOR','ASISTENTE')),
    activo   BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cita (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    dni_paciente CHAR(8) NOT NULL,
    id_medico    INT NOT NULL,
    id_usuario   INT NOT NULL,              -- quién registró la cita
    fecha        DATE NOT NULL,
    hora         TIME NOT NULL,
    motivo       VARCHAR(200) NOT NULL,
    estado       VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE'
                 CHECK (estado IN ('PENDIENTE','ATENDIDA','CANCELADA')),
    monto        DECIMAL(8,2) NOT NULL,     -- tarifa del médico al registrar la cita
    FOREIGN KEY (dni_paciente) REFERENCES paciente(dni),
    FOREIGN KEY (id_medico)    REFERENCES medico(id),
    FOREIGN KEY (id_usuario)   REFERENCES usuario(id)
);

-- ---------------------- DATOS DE EJEMPLO ----------------------
INSERT INTO especialidad (nombre) VALUES
 ('Medicina General'), ('Pediatría'), ('Cardiología'),
 ('Ginecología'), ('Traumatología'), ('Dermatología');

-- Contraseñas: admin123 y asistente123 (guardadas con SHA-256)
INSERT INTO usuario (dni, nombres, telefono, username, clave, rol) VALUES
 ('70123456', 'Carlos Mendoza Ríos',  '987654321', 'admin',     SHA2('admin123', 256),     'ADMINISTRADOR'),
 ('70234567', 'Lucía Fernández Soto', '976543210', 'asistente', SHA2('asistente123', 256), 'ASISTENTE');

INSERT INTO medico (dni, nombres, telefono, id_especialidad, tarifa) VALUES
 ('40123456', 'Dr. Luis Pérez Gómez',     '965432109', 1,  50.00),
 ('40234567', 'Dra. Rosa Quispe Huamán',  '954321098', 2,  70.00),
 ('40345678', 'Dr. Jorge Salazar Vega',   '943210987', 3, 120.00),
 ('40456789', 'Dra. Ana Torres Cruz',     '932109876', 4,  90.00);

INSERT INTO paciente (dni, nombres, edad, telefono, sexo) VALUES
 ('71000001', 'Ana Ruiz Campos',      30, '912345678', 'F'),
 ('71000002', 'Pedro Castillo León',  45, '923456789', 'M'),
 ('71000003', 'María Vargas Díaz',    8,  '',          'F'),
 ('71000004', 'José Ramírez Paz',     62, '934567890', 'M'),
 ('71000005', 'Carla Núñez Soria',    27, '945678901', 'F'),
 ('71000006', 'Luis Torres Medina',   51, '956789012', 'M');

-- Citas repartidas en distintos días para que los reportes muestren datos
INSERT INTO cita (dni_paciente, id_medico, id_usuario, fecha, hora, motivo, estado, monto) VALUES
 ('71000001', 1, 2, CURDATE(),                          '09:00', 'Dolor de garganta',      'ATENDIDA',  50.00),
 ('71000002', 3, 2, CURDATE(),                          '10:00', 'Control de presión',     'ATENDIDA', 120.00),
 ('71000003', 2, 2, CURDATE(),                          '11:30', 'Control de crecimiento', 'PENDIENTE', 70.00),
 ('71000005', 4, 2, CURDATE(),                          '15:00', 'Control ginecológico',   'CANCELADA', 90.00),
 ('71000004', 3, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '09:30', 'Dolor de pecho',        'ATENDIDA', 120.00),
 ('71000006', 1, 2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:30', 'Fiebre y malestar',     'ATENDIDA',  50.00),
 ('71000001', 4, 2, DATE_SUB(CURDATE(), INTERVAL 3 DAY), '16:00', 'Consulta de rutina',    'CANCELADA', 90.00),
 ('71000002', 1, 2, DATE_SUB(CURDATE(), INTERVAL 9 DAY), '10:30', 'Dolor de espalda',      'ATENDIDA',  50.00),
 ('71000003', 2, 2, DATE_SUB(CURDATE(), INTERVAL 20 DAY),'09:00', 'Vacunas',               'ATENDIDA',  70.00),
 ('71000005', 4, 2, DATE_SUB(CURDATE(), INTERVAL 45 DAY),'14:00', 'Control prenatal',       'ATENDIDA',  90.00),
 ('71000004', 3, 2, DATE_SUB(CURDATE(), INTERVAL 70 DAY),'11:00', 'Electrocardiograma',     'ATENDIDA', 120.00),
 ('71000006', 1, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00', 'Chequeo general',        'PENDIENTE', 50.00),
 ('71000002', 3, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:30', 'Control cardiológico',   'PENDIENTE', 120.00);
