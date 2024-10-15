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
public class TarjetaValidator {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);

    /**
     * Valida una tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param tarjeta tarjeta de credito a validar
     * @return un Either con el resultado de la validacion, con el valor derecho
     *         siendo la tarjeta de credito valida y el valor izquierdo siendo un
     *         TarjetaErrors con el error de validacion
     */

    public Either<TarjetaErrors, TarjetaCredito> validarTarjetaCredito(TarjetaCredito tarjeta){
        logger.debug("Validando tarjeta de credito");
        if(!validateNumero((tarjeta.getNumero()))){
            logger.error("Numero de tarjeta invalido");
            return Either.left(new TarjetaErrors.NumeroInvalido("Numero de tarjeta invalido"));
        }
        if(!validateCaducidad(tarjeta.getFechaCaducidad().toString())){
            logger.error("Fecha de caducidad invalida");
            return Either.left(new TarjetaErrors.CaducidadInvalida("Fecha de caducidad invalida"));
        }
        if(!validateNombre(tarjeta.getNombreTitular())){
            logger.error("Nombre del titular invalido");
            return Either.left(new TarjetaErrors.NombreInvalido("Nombre del titular invalido"));
        }
        return Either.right(tarjeta);
    }

    /**
     * Valida el numero de la tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param numero numero de la tarjeta de credito
     * @return true si el numero es valido, false si no lo es
     */

    private boolean validateNumero(String numero){
        logger.debug("Validando numero de tarjeta");
        String tarjetaLimpia = numero.replace(" ", "");
        if(tarjetaLimpia.length() < 13 || tarjetaLimpia.length() > 19) return false;
        if(!tarjetaLimpia.chars().allMatch(Character::isDigit)) return false;
        int[] numeros = tarjetaLimpia.chars().map(Character::getNumericValue).toArray();
        int suma = 0;
        for(int i = numeros.length - 1; i >= 0; i--){
            int digito = numeros[i];
            if((numeros.length - 1 - i) % 2 == 1){
                digito *= 2;
                if(digito > 9) digito -= 9;
            }
            suma += digito;
        }
        return suma % 10 == 0;
    }

    /**
     * Valida el nombre del titular de la tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param nombre nombre del titular de la tarjeta de credito
     * @return true si el nombre es valido, false si no lo es
     */

    private boolean validateNombre(String nombre){
        logger.debug("Validando nombre del titular");
        if(nombre.length() < 2 || nombre.length() > 40){
            logger.error("Nombre del titular invalido");
            return false;
        }
        logger.info("El nombre del titular es valido");
        return true;
    }

    /**
     * Valida la fecha de caducidad de la tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param fechaCaducidad fecha de caducidad de la tarjeta de credito
     * @return true si la fecha es valida, false si no lo es
     */

    private boolean validateCaducidad(String fechaCaducidad){
        logger.debug("Validando fecha de caducidad");
        String[] partes = fechaCaducidad.split("/");
        if (!fechaCaducidad.contains("/")){
            return false;
        }
        int mes = Integer.parseInt(partes[0]);
        int anyo = Integer.parseInt(partes[1]);
        if(mes < 1 || mes > 12 || anyo < 24){
            logger.error("Fecha de caducidad invalida");
            return false;
        }
        logger.info("La fecha de caducidad es valida");
        return true;
    }
}
