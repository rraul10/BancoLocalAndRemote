package org.example.users.validator;

import io.vavr.control.Either;
import org.example.models.Usuario;
import org.example.users.errors.UserErrors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorImplTest {
    UserValidatorImpl userValidatorImpl = new UserValidatorImpl();
    Usuario usuario = Usuario.builder()
            .id(1L)
            .name("John Doe")
            .username("johndoe")
            .email("johndoe@example.com")
            .build();

    @Test
    void validateUser() {
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuario);
        assertTrue(result.isRight());
    }

    @Test
    void validateUserEmptyName() {
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("")
                .username("johndoe")
                .email("johndoe@example.com")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserNameUnderLimit(){
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("jo")
                .username("johndoe")
                .email("johndoe@example.com")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserUserNameUnderLimit(){
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("john")
                .username("jo")
                .email("johndoe@example.com")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre de usuario inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserEmptyUsername() {
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("john")
                .username("")
                .email("johndoe@example.com")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Nombre de usuario inválido, el nombre debe tener al menos 3 caracteres", result.getLeft().getMessage());
    }

    @Test
    void validateUserInvalidEmail() {
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("john")
                .username("jondoe")
                .email("johndoeexample.com")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Email de usuario inválido", result.getLeft().getMessage());
    }

    @Test
    void validateUserEmptyEmail() {
        Usuario usuarioTest = Usuario.builder()
                .id(1L)
                .name("john")
                .username("jondoe")
                .email("")
                .build();
        Either<UserErrors,Usuario> result = userValidatorImpl.ValidateUser(usuarioTest);
        assertTrue(result.isLeft());
        assertEquals("ERROR: Email de usuario inválido", result.getLeft().getMessage());
    }
}