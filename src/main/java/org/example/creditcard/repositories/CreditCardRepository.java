package org.example.creditcard.repositories;

import org.example.models.TarjetaCredito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditCardRepository {

    List<TarjetaCredito> getAll();

    Optional<TarjetaCredito> getById(UUID id);

    TarjetaCredito create(TarjetaCredito creditcard);

    TarjetaCredito update(UUID id, TarjetaCredito creditcard);

    boolean delete(UUID id);
}
