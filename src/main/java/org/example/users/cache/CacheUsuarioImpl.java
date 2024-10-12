package org.example.users.cache;

import org.example.cache.CacheGeneric;
import org.example.models.Usuario;
import org.slf4j.Logger;

public class CacheUsuarioImpl extends CacheGeneric<Long, Usuario> implements CacheUsuario {
    public CacheUsuarioImpl(int maxCapacity, Logger logger) {
        super(maxCapacity, logger);
    }
}
