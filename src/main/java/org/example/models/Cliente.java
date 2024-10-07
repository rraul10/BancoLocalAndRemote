package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
public class Cliente {
    private final Usuario usuario;
    @Nullable
    private final List<TarjetaCredito> tarjetas;
    public UUID getId(){
        return usuario.getId();
    }
    @Nullable
    public List<TarjetaCredito> getTarjetas() {
        return tarjetas;
    }

}


