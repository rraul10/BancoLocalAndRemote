package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Data
@Builder
@Getter
@Setter
public class TarjetaCredito {
    public static final UUID NEW_ID = randomUUID();
    @Builder.Default
    UUID id = NEW_ID;
    @NonNull
    String numero;
    @NonNull
    String nombreTitular;
    @NonNull
    Long clientID;
    @NonNull
    String fechaCaducidad;
    @NonNull
    LocalDateTime createdAt;
    @NonNull
    LocalDateTime updatedAt;
    @NonNull
    Boolean isDeleted;

    // Constructor predeterminado
    public TarjetaCredito() {
    }

    // Constructor completo para Jackson
    @JsonCreator
    public TarjetaCredito(@JsonProperty("id") UUID id,
                          @JsonProperty("numero") @NonNull String numero,
                          @JsonProperty("nombreTitular") @NonNull String nombreTitular,
                          @JsonProperty("clientID") @NonNull Long clientID,
                          @JsonProperty("fechaCaducidad") @NonNull String fechaCaducidad,
                          @JsonProperty("createdAt") @NonNull LocalDateTime createdAt,
                          @JsonProperty("updatedAt") @NonNull LocalDateTime updatedAt,
                          @JsonProperty("isDeleted") @NonNull Boolean isDeleted) {
        this.id = id;
        this.numero = numero;
        this.nombreTitular = nombreTitular;
        this.clientID = clientID;
        this.fechaCaducidad = fechaCaducidad;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = isDeleted;
    }
}


