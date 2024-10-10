package org.example.usuarios.validator;

import io.vavr.control.Either;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.models.Usuario;
import org.example.usuarios.errors.UserErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidator {
    private final Logger logger = LoggerFactory.getLogger(UserValidator.class);

    public Either<UserErrors, Usuario> ValidateUser(Usuario usuario){
        logger.debug("Validando usuario");
        if(!validateNombre(usuario.getNombre())){
            logger.error("Nombre de usuario inválido");
            return Either.left(new UserErrors.NombreInvalido("Nombre inválido, el nombre debe tener al menos 3 caracteres"));
        }
        if(!validateNombre(usuario.getUsername())){
            logger.error("Nombre de usuario inválido");
            return Either.left(new UserErrors.NombreInvalido("Nombre de usuario inválido, el nombre debe tener al menos 3 caracteres"));
        }
        if(!validateEmail(usuario.getEmail())){
            logger.error("Email de usuario inválido");
            return Either.left(new UserErrors.EmailInvalido("Email de usuario inválido"));
        }
        return Either.right(usuario);
    }
    private boolean validateNombre(String nombre) {
        logger.debug("Validando nombre de usuario");
        return nombre != null && nombre.length() > 2;
    }

    private boolean validateEmail(String email) {
        logger.debug("Validando email de usuario");
        String regex = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        return email != null && !email.isEmpty() && email.matches(regex);
    }
}
