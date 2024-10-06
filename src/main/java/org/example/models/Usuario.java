package org.example.models;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class Usuario {
    public static UUID NEW_ID = UUID.randomUUID();
    @Getter
    @Builder.Default
    private UUID id = NEW_ID;
    @NonNull
    private String name;
    @NonNull
    private String username;
    @Getter
    @NonNull
    private String email;

    public String getNombre() {
        return name;
    }

}
