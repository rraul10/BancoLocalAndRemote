package org.example.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class Usuario {
    @Getter
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String username;
    @Getter
    @NonNull
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonCreator
    public Usuario(@JsonProperty("id") Long id,
                   @JsonProperty("name") @NonNull String name,
                   @JsonProperty("username") @NonNull String username,
                   @JsonProperty("email") @NonNull String email,
                   @JsonProperty("createdAt") LocalDateTime createdAt,
                   @JsonProperty("updatedAt") LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getNombre() {
        return name;
    }
}