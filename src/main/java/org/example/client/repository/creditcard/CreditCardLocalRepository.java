package org.example.client.repository.creditcard;

import org.example.models.TarjetaCredito;

import java.util.List;
import java.util.UUID;

public interface CreditCardLocalRepository {
    List<TarjetaCredito> findAllCreditCards();
    TarjetaCredito findCreditCardById(UUID id);
    TarjetaCredito findCreditCardByNumber(String number);;
    TarjetaCredito saveCreditCard(TarjetaCredito creditCard);
    TarjetaCredito updateCreditCard(UUID uuid,TarjetaCredito creditCard);
    Boolean deleteCreditCard(UUID id);
    Boolean deleteAllCreditCards();
    List<TarjetaCredito> findAllCreditCardsByUserId(Long userId);
}
