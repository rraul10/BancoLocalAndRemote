package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private String name;
    private String username;
    private String email;

}
