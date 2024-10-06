package org.example.client.repository;

import org.example.models.TarjetaCredito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditCardRepository {
    List<TarjetaCredito> findAllCreditCards();
    TarjetaCredito findCreditCardById(UUID id);
    TarjetaCredito findCreditCardByNumber(String number);;
    TarjetaCredito saveCreditCard(TarjetaCredito creditCard);
    TarjetaCredito updateCreditCard(TarjetaCredito creditCard);
    Boolean deleteCreditCard(UUID id);
    Boolean deleteAllCreditCards();
    Optional<List<TarjetaCredito>> findAllCreditCardsByUserId(UUID userId);
}
