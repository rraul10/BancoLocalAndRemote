package org.example.users.dto;

import lombok.NonNull;

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

}

