-- Datos iniciales para Clinica Regional
--                          Archivo: data.sql
-- ===========================================

-- Roles
INSERT INTO roles (nombre, descripcion) VALUES ('ADMIN', 'Administrador del sistema');
INSERT INTO roles (nombre, descripcion) VALUES ('RECEPCIONISTA', 'Recepcionista de la clínica');
INSERT INTO roles (nombre, descripcion) VALUES ('MEDICO', 'Médico registrado');
INSERT INTO roles (nombre, descripcion) VALUES ('PACIENTE', 'Paciente registrado');

-- Usuarios

INSERT INTO usuarios (nombre, correo, contrasena, estado, rol_id) VALUES ('Administrador General', 'admin@clinica.pe', 'admin123', true, 1);
INSERT INTO usuarios (nombre, correo, contrasena, estado, rol_id) VALUES ('Recepcionista Uno', 'recepcion@clinica.pe', 'recep123', true, 2);
INSERT INTO usuarios (nombre, correo, contrasena, estado, rol_id) VALUES ('Dr. Pedro Ruiz', 'medico@clinica.pe', 'medico123', true, 3);
INSERT INTO usuarios (nombre, correo, contrasena, estado, rol_id) VALUES ('Paciente de Prueba', 'paciente@clinica.pe', 'paciente123', true, 4);


-- -- Tipos de documento
-- INSERT INTO tipo_documento (descripcion) VALUES ('DNI');
-- INSERT INTO tipo_documento (descripcion) VALUES ('Carnet de extranjería');
-- INSERT INTO tipo_documento (descripcion) VALUES ('Pasaporte');

-- -- Especialidades
-- INSERT INTO especialidades (nombre, url, descripcion) VALUES ('Cardiología', NULL, 'Atención especializada en el sistema cardiovascular');
-- INSERT INTO especialidades (nombre, url, descripcion) VALUES ('Pediatría', NULL, 'Cuidado médico para niños y adolescentes');
-- INSERT INTO especialidades (nombre, url, descripcion) VALUES ('Dermatología', NULL, 'Diagnóstico y tratamiento de enfermedades de la piel');

-- -- Servicios
-- INSERT INTO servicios (nombre) VALUES ('Consulta General');
-- INSERT INTO servicios (nombre) VALUES ('Control Cardiológico');
-- INSERT INTO servicios (nombre) VALUES ('Ecografía');
-- INSERT INTO servicios (nombre) VALUES ('Laboratorio');

-- -- Seguros
-- INSERT INTO seguros (nombre, url, logo, descripcion) VALUES ('ESSALUD', NULL, NULL, 'Seguro social del Perú');
-- INSERT INTO seguros (nombre, url, logo, descripcion) VALUES ('Particular', NULL, NULL, 'Atención directa sin seguro');
-- INSERT INTO seguros (nombre, url, logo, descripcion) VALUES ('RIMAC', NULL, NULL, 'Seguro privado');

-- -- Coberturas
-- INSERT INTO coberturas (descripcion) VALUES ('Cobertura total');
-- INSERT INTO coberturas (descripcion) VALUES ('Cobertura parcial');
-- INSERT INTO coberturas (descripcion) VALUES ('Sin cobertura');

-- -- Asociación servicio - seguro - cobertura
-- INSERT INTO servicios_seguros (id_servicio, id_seguro, id_cobertura) VALUES (1, 1, 1);
-- INSERT INTO servicios_seguros (id_servicio, id_seguro, id_cobertura) VALUES (2, 2, 2);
-- INSERT INTO servicios_seguros (id_servicio, id_seguro, id_cobertura) VALUES (3, 3, 2);

-- -- Asociación seguro - cobertura
-- INSERT INTO seguros_coberturas (id_seguro, id_cobertura) VALUES (1, 1);
-- INSERT INTO seguros_coberturas (id_seguro, id_cobertura) VALUES (2, 2);
-- INSERT INTO seguros_coberturas (id_seguro, id_cobertura) VALUES (3, 2);
