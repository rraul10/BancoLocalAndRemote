package org.example.creditcard.cache;

import org.example.cache.Cache;
import org.example.models.TarjetaCredito;

import java.util.UUID;

public interface CacheTarjeta extends Cache<UUID, TarjetaCredito> {
    int USUARIOS_CACHE_SIZE = 10;
}
