package org.example.creditcard.validators;

import io.vavr.control.Either;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validators.errors.TarjetaErrors;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarjetaValidator {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);

    public Either<TarjetaErrors, TarjetaCredito> validarTarjetaCredito(TarjetaCredito tarjeta){
        logger.debug("Validando tarjeta de crédito");
        if(!validateNumero((tarjeta.getNumero()))){
            logger.error("Número de tarjeta inválido");
            return Either.left(new TarjetaErrors.NumeroInvalido("Número de tarjeta inválido"));
        }
        if(!validateCaducidad(tarjeta.getFechaCaducidad().toString())){
            logger.error("Fecha de caducidad inválida");
            return Either.left(new TarjetaErrors.CaducidadInvalida("Fecha de caducidad inválida"));
        }
        return Either.right(tarjeta);
    }

    private boolean validateNumero(String numero){
        logger.debug("Validando número de tarjeta");
        String tarjetaLimpia = numero.replace("", "");
        if(tarjetaLimpia.length() < 13 || tarjetaLimpia.length() > 19) return false;
        if(!tarjetaLimpia.chars().allMatch(Character::isDigit)) return false;
        int[] numeros = tarjetaLimpia.chars().map(Character::getNumericValue).toArray();
        int suma = 0;
        for(int i = numeros.length - 1; i >= 0; i --){
            int digito = numeros[i];
            if((numeros.length - 1 - i) % 2 == 1){
                digito *= 2;
                if(digito > 9) digito -= 9;
            }
            suma += digito;
        }
        return suma % 10 == 0;
    }

    private boolean validateCaducidad(String fechaCaducidad){
        logger.debug("Validando fecha de caducidad");
        String[] partes = fechaCaducidad.split("/");
        int mes = Integer.parseInt(partes[0]);
        int año = Integer.parseInt(partes[1]);
        if(mes < 1 || mes > 12 || año < 24){
            logger.error("Fecha de caducidad inválida");
            return false;
        }
        logger.info("La fecha de caducidad es válida");
        return true;
    }
}
