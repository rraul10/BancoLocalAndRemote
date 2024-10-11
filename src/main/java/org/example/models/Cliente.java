package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class Cliente {
    private long id;
    public  Usuario usuario;
    @Nullable
    public List<TarjetaCredito> tarjetas;
    public Long getId(){
        return usuario.getId();
    }
    @Nullable
    public List<TarjetaCredito> getTarjetas() {
        return tarjetas;
    }

    public Cliente(Usuario usuario, List<TarjetaCredito> tarjetas) {
        this.id = usuario.getId();
        this.usuario = usuario;
        this.tarjetas = tarjetas;
    }
}


