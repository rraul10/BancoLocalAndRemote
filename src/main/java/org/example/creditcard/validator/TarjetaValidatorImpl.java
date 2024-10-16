package org.example.creditcard.validator;

import io.vavr.control.Either;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarjetaValidatorImpl extends TarjetaValidator {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    @Override
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
        if(!validateNombre(tarjeta.getNombreTitular())){
            logger.error("Nombre del titular inválido");
            return Either.left(new TarjetaErrors.NombreInvalido("Nombre del titular inválido"));
        }
        return Either.right(tarjeta);
    }

    private boolean validateNumero(String numero){
        logger.debug("Validando número de tarjeta");
        String tarjetaLimpia = numero.replace(" ", "");
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

    private boolean validateNombre(String nombre){
        logger.debug("Validando nombre del titular");
        if(nombre.length() < 2 || nombre.length() > 40){
            logger.error("Nombre del titular inválido");
            return false;
        }
        logger.info("El nombre del titular es válido");
        return true;
    }

    private boolean validateCaducidad(String fechaCaducidad){
        logger.debug("Validando fecha de caducidad");
        String[] partes = fechaCaducidad.split("/");
        if (!fechaCaducidad.contains("/")){
            return false;
        }
        int mes = Integer.parseInt(partes[0]);
        int anyo = Integer.parseInt(partes[1]);
        if(mes < 1 || mes > 12 || anyo < 24){
            logger.error("Fecha de caducidad inválida");
            return false;
        }
        logger.info("La fecha de caducidad es válida");
        return true;
    }
}
