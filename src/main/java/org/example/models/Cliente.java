package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import reactor.util.annotation.Nullable;

import java.util.List;

@Data
@Builder
@Getter
public class Cliente {
    public final Usuario usuario;
    @Nullable
    public List<TarjetaCredito> tarjetas;
    public Long getId(){
        return usuario.getId();
    }
    @Nullable
    public List<TarjetaCredito> getTarjetas() {
        return tarjetas;
    }

}



