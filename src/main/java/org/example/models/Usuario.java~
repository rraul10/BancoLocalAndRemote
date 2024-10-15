package org.example.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class Usuario {
    @Getter
<<<<<<< HEAD
    private Long id;
=======
    @Builder.Default
    private UUID id = NEW_ID;
    @NonNull
>>>>>>> origin/develop
    private String name;
    @NonNull
    private String username;
    @Getter
    @NonNull
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getNombre() {
        return name;
    }

}
