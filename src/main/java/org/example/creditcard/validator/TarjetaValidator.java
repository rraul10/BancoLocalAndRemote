package org.example.creditcard.validator;

import io.vavr.control.Either;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validador de tarjetas de credito.
 * Esta clase proporciona metodos para validar tarjetas de credito.
 * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
 * @since 1.0
 */
public interface TarjetaValidator {

    public Either<TarjetaErrors, TarjetaCredito> validarTarjetaCredito(TarjetaCredito tarjeta);

}
