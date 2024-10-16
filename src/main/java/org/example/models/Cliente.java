package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import reactor.util.annotation.Nullable;

import java.util.List;

@Data
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

    @JsonCreator
    public Cliente(@JsonProperty("usuario") Usuario usuario,  @JsonProperty("tarjetas") @Nullable List<TarjetaCredito> tarjetas) {
        this.usuario = usuario;
        this.tarjetas = tarjetas;
    }

}



