CREATE TABLE provincia (
                           codigo BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL
);

CREATE TABLE localidad (
                           codigo BIGSERIAL PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           provincia_codigo BIGINT NOT NULL,
                           FOREIGN KEY (provincia_codigo) REFERENCES provincia(codigo)
);

CREATE TABLE estacion (
                          cod_estacion BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(255),
                          tipo VARCHAR(50),
                          direccion VARCHAR(255),
                          codigo_postal VARCHAR(20),
                          longitud DOUBLE PRECISION,
                          latitud DOUBLE PRECISION,
                          descripcion TEXT,
                          horario VARCHAR(255),
                          contacto VARCHAR(255),
                          url VARCHAR(255),
                          localidad_id BIGINT NOT NULL,
                          FOREIGN KEY (localidad_id) REFERENCES localidad(codigo) ON DELETE CASCADE
);