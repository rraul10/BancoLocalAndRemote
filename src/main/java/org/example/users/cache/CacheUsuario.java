package org.example.users.cache;

import org.example.cache.Cache;
import org.example.models.Usuario;

public interface CacheUsuario extends Cache<Long, Usuario> {
    int USUARIOS_CACHE_SIZE = 10;
}
