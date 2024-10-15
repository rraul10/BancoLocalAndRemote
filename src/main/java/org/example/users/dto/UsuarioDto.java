package org.example.users.dto;

import lombok.NonNull;
import org.example.models.Usuario;

public record UsuarioDto (
        long id,
        @NonNull
        String name,
        @NonNull
        String username,
        @NonNull
        String email,
        String createdAt,
        String updatedAt
){
        public UsuarioDto(Usuario usuario) {
                this(
                        usuario.getId(),
                        usuario.getName(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getCreatedAt().toString(),
                        usuario.getUpdatedAt().toString()
                );
        }


}

