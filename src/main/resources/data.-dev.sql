
-- Datos iniciales para Clinica Regional
--                          Archivo: data.sql
-- ===========================================

-- Roles
INSERT INTO rol (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('RECEPCIONISTA', 'Recepcionista de la clínica'),
('MEDICO', 'Médico registrado'),
('PACIENTE', 'Paciente registrado');

-- Tipos de documento
INSERT INTO tipo_documento (descripcion) VALUES
('DNI'),
('Carnet de extranjería'),
('Pasaporte');

-- Usuarios base
INSERT INTO usuario (nombre, correo, contrasena, estado, id_rol) VALUES
('Administrador General', 'admin@clinica.pe', 'admin123', true, 1),
('Recepcionista Uno', 'recepcion@clinica.pe', 'recep123', true, 2),
('Dr. Pedro Ruiz', 'medico@clinica.pe', 'medico123', true, 3),
('Paciente de Prueba', 'paciente@clinica.pe', 'paciente123', true, 4);

-- Especialidades
INSERT INTO especialidad (nombre, url, descripcion) VALUES
('Cardiología', NULL, 'Atención especializada en el sistema cardiovascular'),
('Pediatría', NULL, 'Cuidado médico para niños y adolescentes'),
('Dermatología', NULL, 'Diagnóstico y tratamiento de enfermedades de la piel');

-- Servicios
INSERT INTO servicio (nombre) VALUES
('Consulta General'),
('Control Cardiológico'),
('Ecografía'),
('Laboratorio');

-- Seguros
INSERT INTO seguro (nombre, url, logo, descripcion) VALUES
('ESSALUD', NULL, NULL, 'Seguro social del Perú'),
('Particular', NULL, NULL, 'Atención directa sin seguro'),
('RIMAC', NULL, NULL, 'Seguro privado');

-- Coberturas
INSERT INTO cobertura (descripcion) VALUES
('Cobertura total'),
('Cobertura parcial'),
('Sin cobertura');

-- Asociación servicio - seguro - cobertura
INSERT INTO servicio_seguro (id_servicio, id_seguro, id_cobertura) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 2);

-- Asociación seguro - cobertura
INSERT INTO seguro_cobertura (id_seguro, id_cobertura) VALUES
(1, 1),
(2, 2),
(3, 2);
