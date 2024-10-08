-- Eliminar las tablas si existen
DROP TABLE IF EXISTS Tarjeta;
DROP TABLE IF EXISTS Cliente;

-- Crear la tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE
    );


-- Crear la tabla Tarjeta
CREATE TABLE IF NOT EXISTS Tarjeta (
                                       id TEXT PRIMARY KEY DEFAULT (lower(hex(randomblob(16)))), -- Generar un UUID como texto
    numero VARCHAR(16) NOT NULL,
    clientID TEXT NOT NULL, -- Cambiar a TEXT para el UUID
    fechaCaducidad DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    isDeleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (clientID) REFERENCES Cliente(id) ON DELETE CASCADE
    );
