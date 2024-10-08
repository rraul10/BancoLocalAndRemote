-- Eliminar las tablas si ya existen
DROP TABLE IF EXISTS Tarjeta;
DROP TABLE IF EXISTS Cliente;

-- Crear la tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE
    );

-- Crear la tabla Tarjeta
CREATE TABLE IF NOT EXISTS Tarjeta (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    numero VARCHAR(16) NOT NULL,
    clienteID UUID NOT NULL,
    fechaCaducidad DATE NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (clienteID) REFERENCES Cliente(id) ON DELETE CASCADE
    );

