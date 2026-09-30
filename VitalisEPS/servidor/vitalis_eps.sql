-- ============================================================================
--  Vitalis EPS · Base de datos de citas médicas (MySQL / MariaDB de XAMPP)
--  Importar en phpMyAdmin: pestaña "Importar" → elegir este archivo → Continuar.
--  Crea la base vitalis_eps con sus tablas y datos de ejemplo.
-- ============================================================================

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS vitalis_eps
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vitalis_eps;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS preparaciones;
DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS profesionales;
DROP TABLE IF EXISTS especialidades;
DROP TABLE IF EXISTS sedes;
DROP TABLE IF EXISTS afiliados;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE afiliados (
  id_afiliado  INT AUTO_INCREMENT PRIMARY KEY,
  documento    VARCHAR(20)  NOT NULL UNIQUE,
  nombre       VARCHAR(100) NOT NULL,
  tipo         ENUM('Cotizante', 'Beneficiario') NOT NULL DEFAULT 'Cotizante'
) ENGINE = InnoDB;

CREATE TABLE especialidades (
  id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
  codigo          VARCHAR(30)  NOT NULL UNIQUE,  -- Debe coincidir con el enum Especialidad de la app.
  nombre          VARCHAR(60)  NOT NULL
) ENGINE = InnoDB;

CREATE TABLE profesionales (
  id_profesional  INT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(100) NOT NULL,
  registro        VARCHAR(30)  NOT NULL,
  id_especialidad INT NOT NULL,
  FOREIGN KEY (id_especialidad) REFERENCES especialidades (id_especialidad)
) ENGINE = InnoDB;

CREATE TABLE sedes (
  id_sede   INT AUTO_INCREMENT PRIMARY KEY,
  nombre    VARCHAR(60)  NOT NULL,
  direccion VARCHAR(120) NOT NULL DEFAULT ''
) ENGINE = InnoDB;

-- Tabla maestro de la app: una fila por cita.
CREATE TABLE citas (
  id_cita          INT AUTO_INCREMENT PRIMARY KEY,
  id_afiliado      INT NOT NULL,
  id_profesional   INT NOT NULL,
  id_sede          INT NOT NULL,
  fecha            DATE NOT NULL,
  hora             TIME NOT NULL,
  duracion_min     SMALLINT NOT NULL,
  modalidad        ENUM('Presencial', 'Telemedicina') NOT NULL DEFAULT 'Presencial',
  lugar            VARCHAR(120) NOT NULL,
  estado           ENUM('Confirmada', 'PorConfirmar', 'Atendida', 'Cancelada') NOT NULL DEFAULT 'PorConfirmar',
  motivo           VARCHAR(255) NOT NULL,
  autorizacion     VARCHAR(30)  NOT NULL,
  cuota_moderadora INT NOT NULL DEFAULT 0,
  FOREIGN KEY (id_afiliado)    REFERENCES afiliados (id_afiliado),
  FOREIGN KEY (id_profesional) REFERENCES profesionales (id_profesional),
  FOREIGN KEY (id_sede)        REFERENCES sedes (id_sede)
) ENGINE = InnoDB;

-- Tabla detalle: los pasos de preparación de cada cita (una cita tiene muchos pasos).
CREATE TABLE preparaciones (
  id_preparacion INT AUTO_INCREMENT PRIMARY KEY,
  id_cita        INT NOT NULL,
  orden          TINYINT NOT NULL,
  texto          VARCHAR(160) NOT NULL,
  FOREIGN KEY (id_cita) REFERENCES citas (id_cita) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
--  Datos de ejemplo (ficticios)
-- ----------------------------------------------------------------------------

INSERT INTO afiliados (id_afiliado, documento, nombre, tipo) VALUES
  (1, '1020456789', 'Camilo Casallas', 'Cotizante');

INSERT INTO especialidades (id_especialidad, codigo, nombre) VALUES
  (1, 'MedicinaGeneral', 'Medicina general'),
  (2, 'Odontologia',     'Odontología'),
  (3, 'Psicologia',      'Psicología'),
  (4, 'Cardiologia',     'Cardiología'),
  (5, 'Laboratorio',     'Laboratorio clínico'),
  (6, 'Oftalmologia',    'Oftalmología'),
  (7, 'Dermatologia',    'Dermatología'),
  (8, 'Pediatria',       'Pediatría'),
  (9, 'Fisioterapia',    'Fisioterapia');

INSERT INTO profesionales (id_profesional, nombre, registro, id_especialidad) VALUES
  (1, 'Dra. Laura Méndez',   'RM 52.184',          1),
  (2, 'Dr. Santiago Rojas',  'RM 77.410',          2),
  (3, 'Dra. Mariana Ortiz',  'TP 118.502',         3),
  (4, 'Dr. Felipe Castaño',  'RM 40.967',          4),
  (5, 'Toma de muestras',    'Laboratorio Vitalis', 5),
  (6, 'Dra. Paula Herrera',  'RM 63.221',          6),
  (7, 'Dr. Julián Pardo',    'RM 58.340',          7),
  (8, 'Ft. Daniel Suárez',   'TP 30.518',          9),
  (9, 'Dra. Catalina Vega',  'RM 49.875',          8);

INSERT INTO sedes (id_sede, nombre, direccion) VALUES
  (1, 'Sede Chapinero', 'Cl. 63 # 13-22'),
  (2, 'Sede Usaquén',   'Cra. 7 # 119-14'),
  (3, 'Sede Salitre',   'Av. Cl. 26 # 68C-61'),
  (4, 'Videollamada',   '');

INSERT INTO citas (id_cita, id_afiliado, id_profesional, id_sede, fecha, hora, duracion_min, modalidad, lugar, estado, motivo, autorizacion, cuota_moderadora) VALUES
  (1, 1, 1, 1, '2026-10-01', '07:30:00', 20, 'Presencial',   'Consultorio 204 · Cl. 63 # 13-22',        'Confirmada',   'Control de rutina y revisión de los exámenes de sangre del mes pasado.', 'AUT-2026-58213', 5200),
  (2, 1, 2, 2, '2026-10-02', '10:00:00', 40, 'Presencial',   'Consultorio 12 · Cra. 7 # 119-14',        'PorConfirmar', 'Limpieza dental semestral y valoración de sensibilidad en muelas.',      'AUT-2026-58877', 5200),
  (3, 1, 3, 4, '2026-10-05', '16:30:00', 50, 'Telemedicina', 'El enlace se activa 10 minutos antes',    'Confirmada',   'Sesión de seguimiento del manejo del estrés académico.',                 'AUT-2026-59002', 5200),
  (4, 1, 4, 3, '2026-10-07', '14:15:00', 30, 'Presencial',   'Torre B, piso 5 · Av. Cl. 26 # 68C-61',   'PorConfirmar', 'Valoración por palpitaciones ocasionales y lectura de electrocardiograma.', 'AUT-2026-59318', 5200),
  (5, 1, 5, 1, '2026-10-10', '06:30:00', 15, 'Presencial',   'Primer piso · Cl. 63 # 13-22',            'Confirmada',   'Cuadro hemático, perfil lipídico y glicemia.',                           'AUT-2026-59480', 0),
  (6, 1, 6, 3, '2026-10-13', '09:00:00', 30, 'Presencial',   'Torre A, piso 3 · Av. Cl. 26 # 68C-61',   'PorConfirmar', 'Revisión de fórmula de lentes y control de visión borrosa de lejos.',    'AUT-2026-59711', 5200),
  (7, 1, 7, 2, '2026-09-22', '11:20:00', 20, 'Presencial',   'Consultorio 31 · Cra. 7 # 119-14',        'Atendida',     'Control de dermatitis en manos.',                                        'AUT-2026-57102', 5200),
  (8, 1, 8, 1, '2026-09-10', '17:00:00', 45, 'Presencial',   'Gimnasio terapéutico · Cl. 63 # 13-22',   'Atendida',     'Terapia física para dolor lumbar (sesión 4 de 10).',                     'AUT-2026-56390', 5200),
  (9, 1, 9, 3, '2026-09-07', '08:40:00', 20, 'Presencial',   'Torre A, piso 2 · Av. Cl. 26 # 68C-61',   'Cancelada',    'Control de crecimiento y desarrollo.',                                   'AUT-2026-55811', 0);

INSERT INTO preparaciones (id_cita, orden, texto) VALUES
  (1, 1, 'Llega 15 minutos antes para el registro'),
  (1, 2, 'Trae tu documento de identidad'),
  (1, 3, 'Lleva los resultados de laboratorio impresos o en la app'),
  (2, 1, 'Cepíllate antes de la cita'),
  (2, 2, 'Si tomas anticoagulantes, avísale al odontólogo'),
  (3, 1, 'Busca un lugar tranquilo y con buena señal'),
  (3, 2, 'Usa audífonos para más privacidad'),
  (3, 3, 'Ten a mano tus notas de la semana'),
  (4, 1, 'No tomes café ni bebidas energizantes 12 horas antes'),
  (4, 2, 'Trae el electrocardiograma anterior'),
  (4, 3, 'Usa ropa cómoda'),
  (5, 1, 'Ayuno de 8 a 12 horas (solo agua)'),
  (5, 2, 'Trae la orden médica'),
  (5, 3, 'No hagas ejercicio fuerte el día anterior'),
  (6, 1, 'Trae tus gafas actuales'),
  (6, 2, 'Ve acompañado: pueden dilatarte las pupilas'),
  (7, 1, 'Trae los medicamentos que estás usando'),
  (8, 1, 'Usa ropa deportiva'),
  (9, 1, 'Trae el carné de vacunación');
