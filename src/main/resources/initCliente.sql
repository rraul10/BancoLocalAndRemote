-- Eliminar las tablas si existen
DROP TABLE IF EXISTS Tarjeta;
DROP TABLE IF EXISTS Cliente;

-- Crear la tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
     id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))), -- Generar un UUID como texto
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
    );

-- Crear la tabla Tarjeta
CREATE TABLE IF NOT EXISTS Tarjeta (
                                       id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))), -- Generar un UUID como texto
    numero VARCHAR(16) NOT NULL,
    clientID TEXT NOT NULL, -- Cambiar a TEXT para el UUID
    fechaCaducidad DATE NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (clientID) REFERENCES Cliente(id) ON DELETE CASCADE
    );
