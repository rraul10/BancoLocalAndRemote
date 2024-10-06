package org.example.mappers;

import org.example.api.getAll.ResponseUserGetAll;
import org.example.models.Usuario;

import java.util.UUID;

public class UserMapper {

    public static Usuario toUserFromCreate(ResponseUserGetAll responseUserGetAll) {
        return Usuario.builder()
                .id(UUID.randomUUID())
                .name(responseUserGetAll.getName())
                .username(responseUserGetAll.getUsername())
                .email(responseUserGetAll.getEmail())
                .build();
    }
}
