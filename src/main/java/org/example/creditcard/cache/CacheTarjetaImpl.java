package org.example.creditcard.cache;

import org.example.cache.CacheGeneric;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;

import java.util.UUID;

public class CacheTarjetaImpl extends CacheGeneric<UUID, TarjetaCredito> implements CacheTarjeta {
    public CacheTarjetaImpl(int maxCapacity, Logger logger) {
        super(maxCapacity, logger);
    }
}
