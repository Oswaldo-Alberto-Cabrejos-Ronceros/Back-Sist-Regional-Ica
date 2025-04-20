-- Datos iniciales para Clinica Regional
--                          Archivo: data.sql
-- ===========================================

-- Roles
INSERT INTO rol (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO rol (nombre, descripcion) VALUES ('RECEPCIONISTA', 'Recepcionista de la clínica');
INSERT INTO rol (nombre, descripcion) VALUES ('MEDICO', 'Médico registrado');
INSERT INTO rol (nombre, descripcion) VALUES ('PACIENTE', 'Paciente registrado');

-- Usuarios
INSERT INTO usuario (nombre, correo, contraseña, estado, rol_id) VALUES ('Administrador General', 'admin@clinica.pe', 'admin123', true, 1);
INSERT INTO usuario (nombre, correo, contraseña, estado, rol_id) VALUES ('Recepcionista Uno', 'recepcion@clinica.pe', 'recep123', true, 2);
INSERT INTO usuario (nombre, correo, contraseña, estado, rol_id) VALUES ('Dr. Pedro Ruiz', 'medico@clinica.pe', 'medico123', true, 3);
INSERT INTO usuario (nombre, correo, contraseña, estado, rol_id) VALUES ('Paciente de Prueba', 'paciente@clinica.pe', 'paciente123', true, 4);

-- -- Tipos de documento
-- INSERT INTO tipo_documento (descripcion) VALUES ('DNI');
-- INSERT INTO tipo_documento (descripcion) VALUES ('Carnet de extranjería');
-- INSERT INTO tipo_documento (descripcion) VALUES ('Pasaporte');

-- -- Especialidades
-- INSERT INTO especialidad (nombre, url, descripcion) VALUES ('Cardiología', NULL, 'Atención especializada en el sistema cardiovascular');
-- INSERT INTO especialidad (nombre, url, descripcion) VALUES ('Pediatría', NULL, 'Cuidado médico para niños y adolescentes');
-- INSERT INTO especialidad (nombre, url, descripcion) VALUES ('Dermatología', NULL, 'Diagnóstico y tratamiento de enfermedades de la piel');

-- -- Servicios
-- INSERT INTO servicio (nombre) VALUES ('Consulta General');
-- INSERT INTO servicio (nombre) VALUES ('Control Cardiológico');
-- INSERT INTO servicio (nombre) VALUES ('Ecografía');
-- INSERT INTO servicio (nombre) VALUES ('Laboratorio');

-- -- Seguros
-- INSERT INTO seguro (nombre, url, logo, descripcion) VALUES ('ESSALUD', NULL, NULL, 'Seguro social del Perú');
-- INSERT INTO seguro (nombre, url, logo, descripcion) VALUES ('Particular', NULL, NULL, 'Atención directa sin seguro');
-- INSERT INTO seguro (nombre, url, logo, descripcion) VALUES ('RIMAC', NULL, NULL, 'Seguro privado');

-- -- Coberturas
-- INSERT INTO cobertura (descripcion) VALUES ('Cobertura total');
-- INSERT INTO cobertura (descripcion) VALUES ('Cobertura parcial');
-- INSERT INTO cobertura (descripcion) VALUES ('Sin cobertura');

-- -- Asociación servicio - seguro - cobertura
-- INSERT INTO servicio_seguro (id_servicio, id_seguro, id_cobertura) VALUES (1, 1, 1);
-- INSERT INTO servicio_seguro (id_servicio, id_seguro, id_cobertura) VALUES (2, 2, 2);
-- INSERT INTO servicio_seguro (id_servicio, id_seguro, id_cobertura) VALUES (3, 3, 2);

-- -- Asociación seguro - cobertura
-- INSERT INTO seguro_cobertura (id_seguro, id_cobertura) VALUES (1, 1);
-- INSERT INTO seguro_cobertura (id_seguro, id_cobertura) VALUES (2, 2);
-- INSERT INTO seguro_cobertura (id_seguro, id_cobertura) VALUES (3, 2);
