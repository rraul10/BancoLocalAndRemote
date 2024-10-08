package org.example.usuarios.validator;

import io.vavr.control.Either;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Usuario;
import org.example.usuarios.errors.UserErrors;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {
    UserValidator userValidator = new UserValidator();
    Usuario usuario = Usuario.builder()
            .id(UUID.fromString("3ec260b3-820c-496a-8620-9fe3b0b016b3"))
            .name("John Doe")
            .username("johndoe")
            .email("johndoe@example.com")
            .build();

    @Test
    void validateUser() {
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isRight());
    }

    @Test
    void validateUserEmptyName() {
        usuario.setName("");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserNameUnderLimit(){
        usuario.setName("Jo");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserUserNameUnderLimit(){
        usuario.setUsername("jo");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre de usuario inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserEmptyUsername() {
        usuario.setUsername("");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre de usuario inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserInvalidEmail() {
        usuario.setEmail("johndoeexample.com");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Email de usuario inválido", result.getLeft().getMessage());
    }

    @Test
    void validateUserEmptyEmail() {
        usuario.setEmail("");
        Either<UserErrors,Usuario> result = userValidator.ValidateUser(usuario);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Email de usuario inválido", result.getLeft().getMessage());
    }
}