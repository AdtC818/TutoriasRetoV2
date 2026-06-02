CREATE TABLE IF NOT EXISTS materia (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100),
  descripcion TEXT,
  activa BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS bloque_disponibilidad (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tutor_id VARCHAR(100),
  materia_id INT,
  fecha_inicio DATETIME,
  fecha_fin DATETIME,
  estado VARCHAR(20) DEFAULT 'LIBRE',
  FOREIGN KEY (materia_id) REFERENCES materia(id)
);

CREATE TABLE IF NOT EXISTS franja_horaria (
  id INT AUTO_INCREMENT PRIMARY KEY,
  bloque_id INT,
  hora_inicio DATETIME,
  hora_fin DATETIME,
  FOREIGN KEY (bloque_id) REFERENCES bloque_disponibilidad(id)
);

-- Seed de materias iniciales
INSERT IGNORE INTO materia (nombre, descripcion, activa) VALUES
('Calculo Diferencial', 'Limites, derivadas y aplicaciones', true),
('Algebra Lineal', 'Vectores, matrices y transformaciones lineales', true),
('Programacion Orientada a Objetos', 'Java, C++ y patrones de diseno', true),
('Bases de Datos', 'SQL, NoSQL, modelado y optimizacion', true),
('Estructuras de Datos', 'Listas, arboles, grafos y algoritmos', true),
('Calculo Integral', 'Integrales, series y ecuaciones diferenciales', true),
('Redes de Computadores', 'TCP/IP, protocolos y arquitecturas de red', true),
('Ingenieria de Software', 'Metodologias agiles, UML y gestion de proyectos', true);