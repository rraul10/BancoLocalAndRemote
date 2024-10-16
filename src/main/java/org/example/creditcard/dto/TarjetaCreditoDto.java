package org.example.creditcard.dto;

import lombok.NonNull;
import org.example.models.TarjetaCredito;

import java.util.UUID;

public record TarjetaCreditoDto(
        UUID id,
        @NonNull
        String numero,
        @NonNull
        String nombreTitular,
        @NonNull
        Long clientID,
        @NonNull
        String fechaCaducidad,
        String createdAt,
        String updatedAt,
        Boolean isDeleted
) {
    public TarjetaCreditoDto(TarjetaCredito tarjetaCredito) {
        this(
                tarjetaCredito.getId(),
                tarjetaCredito.getNumero(),
                tarjetaCredito.getNombreTitular(),
                tarjetaCredito.getClientID(),
                tarjetaCredito.getFechaCaducidad(),
                tarjetaCredito.getCreatedAt().toString(),
                tarjetaCredito.getUpdatedAt().toString(),
                tarjetaCredito.getIsDeleted()
        );
    }
}
