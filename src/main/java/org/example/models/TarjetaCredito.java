package org.example.models;

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
    UUID clientID;
    @NonNull
    String fechaCaducidad;
    @NonNull
    LocalDateTime createdAt;
    @NonNull
    LocalDateTime updatedAt;
    @NonNull
    boolean isDeleted;
}


