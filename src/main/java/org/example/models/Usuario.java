package org.example.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class Usuario {
    @Getter
    private Long id;
    private String name;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
