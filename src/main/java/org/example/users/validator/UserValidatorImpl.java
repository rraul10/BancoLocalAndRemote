package org.example.users.validator;

import io.vavr.control.Either;
import org.example.models.Usuario;

import org.example.users.errors.UserErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserValidatorImpl implements UserValidator {
    private final Logger logger = LoggerFactory.getLogger(UserValidatorImpl.class);

    /**
     * Valida un usuario
     * @param usuario El usuario a validar
     * @return Either un UserErrors si el usuario es invalido, o el mismo usuario si es valido
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    @Override
    public Either<UserErrors, Usuario> ValidateUser(Usuario usuario) {
        logger.debug("Validando usuario");
        if (!validateNombre(usuario.getNombre())) {
            logger.error("Nombre de usuario invalido");
            return Either.left(new UserErrors.NombreInvalido("Nombre invalido, el nombre debe tener al menos 3 caracteres"));
        }
        if (!validateNombre(usuario.getUsername())) {
            logger.error("Nombre de usuario invalido");
            return Either.left(new UserErrors.NombreInvalido("Nombre de usuario invalido, el nombre debe tener al menos 3 caracteres"));
        }
        if (!validateEmail(usuario.getEmail())) {
            logger.error("Email de usuario invalido");
            return Either.left(new UserErrors.EmailInvalido("Email de usuario invalido"));
        }
        return Either.right(usuario);
    }

    /**
     * Valida un nombre de usuario
     * @param nombre el nombre de usuario a validar
     * @return true si el nombre de usuario es valido, false en caso contrario
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    private boolean validateNombre(String nombre) {
        logger.debug("Validando nombre de usuario");
        return nombre != null && nombre.length() > 2;
    }

    /**
     * Valida el email de un usuario
     * @param email el email del usuario a validar
     * @return true si el email es valido, false en caso contrario
     * @author Javier Hernandez, Yahya El Hadri, Javier Ruiz, Alvaro Herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    private boolean validateEmail(String email) {
        logger.debug("Validando email de usuario");
        String regex = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
        return email != null && !email.isEmpty() && email.matches(regex);
    }

}
