package org.example.creditcard.validator;

import io.vavr.control.Either;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.models.TarjetaCredito;

public interface TarjetaValidator {
    Either<TarjetaErrors, TarjetaCredito> validarTarjetaCredito(TarjetaCredito tarjeta);
}
