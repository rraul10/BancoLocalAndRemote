package org.example.users.validator;

import io.vavr.control.Either;
import org.example.models.Usuario;
import org.example.users.errors.UserErrors;

public interface UserValidator {
    Either<UserErrors, Usuario> ValidateUser(Usuario usuario);

}
