package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
public class Cliente {
    private final Usuario usuario;
    private final List<TarjetaCredito> tarjetas;
    public UUID getId(){
        return usuario.getId();
    }
}


