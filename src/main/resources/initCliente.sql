DROP TABLE IF EXISTS Cliente;
CREATE TABLE IF NOT EXISTS Cliente (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuarioID UUID NOT NULL,
    FOREIGN KEY (usuarioID) REFERENCES Usuario(id),
    FOREIGN KEY (tarjetaID) REFERENCES Tarjeta(id)
);


