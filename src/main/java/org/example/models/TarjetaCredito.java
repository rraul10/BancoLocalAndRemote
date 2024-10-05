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
    private UUID id = NEW_ID;
    @NonNull
    private String numero;
    @NonNull
    private String nombreTitular;
    @NonNull
    private UUID clientID;
    @NonNull
    private LocalDate fechaCaducidad;
    @NonNull
    private LocalDateTime createdAt;
    @NonNull
    private LocalDateTime updatedAt;
    @NonNull
    private boolean isDeleted;
}


