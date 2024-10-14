package org.example.users.validator;

import io.vavr.control.Either;
import org.example.models.Usuario;

import org.example.users.errors.UserErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidatorImpl implements UserValidator {
    private final Logger logger = LoggerFactory.getLogger(UserValidatorImpl.class);

    /**
     * Validates a user
     * @param usuario The user to validate
     * @return Either a UserErrors if the user is invalid, or the user itself if it's valid
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    @Override
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
    /**
     * Validates a user name
     * @param nombre the username to validate
     * @return true if the username is valid, false otherwise
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    private boolean validateNombre(String nombre) {
        logger.debug("Validando nombre de usuario");
        return nombre != null && nombre.length() > 2;
    }

    /**
     * Validates a user email
     * @param email the user email to validate
     * @return true if the user email is valid, false otherwise
     * @author Javier Hernández, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    private boolean validateEmail(String email) {
        logger.debug("Validando email de usuario");
        String regex = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        return email != null && !email.isEmpty() && email.matches(regex);
    }
}
