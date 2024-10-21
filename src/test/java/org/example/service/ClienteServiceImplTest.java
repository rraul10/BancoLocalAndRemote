package org.example.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.notification.Notification;
import org.example.notification.TarjetaNotificacion;
import org.example.notification.UserNotifications;
import org.example.service.errors.ServiceError;
import org.example.users.cache.CacheUsuario;
import org.example.users.errors.UserErrors;
import org.example.users.repository.UserRemoteRepository;
import org.example.users.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private CreditCardLocalRepository creditCardLocalRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private CacheUsuario cacheUsuario;

    @Mock
    private CacheTarjetaImpl cacheTarjeta;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserRemoteRepository userRemoteRepository;

    @Mock
    private TarjetaValidator tarjetaValidator;

    @Mock
    private UserNotifications userNotifications;

    @Mock
    private TarjetaNotificacion tarjetaNotificacion;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Usuario usuario;
    private TarjetaCredito tarjetaCredito;
    private Cliente cliente;

    @Test
    public void test_create_cliente_success() {
        // Arrange
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjeta = new TarjetaCredito(UUID.randomUUID(), "1234567890123456", "John Doe", 1L, "12/25", LocalDateTime.now(), LocalDateTime.now(), false);
        Cliente cliente = new Cliente(usuario, List.of(tarjeta));

        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.right(usuario));

        when(tarjetaValidator.validarTarjetaCredito(any(TarjetaCredito.class))).thenReturn(Either.right(tarjeta));

        when(userRemoteRepository.createUser(any(Usuario.class))).thenReturn(usuario);

        when(creditCardRepository.create(any(TarjetaCredito.class))).thenReturn(tarjeta);

        // Act
        Either<ServiceError, Cliente> result = clienteService.createCliente(cliente);

        // Assert
        assertTrue(result.isRight());
        assertEquals(cliente, result.get());
    }

    // Handles validation errors for user and credit card gracefully
    @Test
    public void test_create_cliente_user_validation_errors() {
        // Arrange
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjeta = new TarjetaCredito(UUID.randomUUID(), "1234567890123456", "John Doe", 1L, "12/25", LocalDateTime.now(), LocalDateTime.now(), false);
        Cliente cliente = new Cliente(usuario, List.of(tarjeta));

        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.left(new UserErrors.NombreInvalido("Invalid name")));

        // Act
        Either<ServiceError, Cliente> result = clienteService.createCliente(cliente);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UservalidatorError);
    }

    // Handles validation errors for user and credit card gracefully
    @Test
    public void test_create_cliente_credit_card_validation_errors() {
        // Arrange
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjeta = new TarjetaCredito(UUID.randomUUID(), "1234567890123456", "John Doe", 1L, "12/25", LocalDateTime.now(), LocalDateTime.now(), false);
        Cliente cliente = new Cliente(usuario, List.of(tarjeta));

        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.right(usuario));

        when(userRemoteRepository.createUser(any(Usuario.class))).thenReturn(usuario);

        when(tarjetaValidator.validarTarjetaCredito(any(TarjetaCredito.class))).thenReturn(Either.left(new TarjetaErrors.NombreInvalido("Invalid card")));

        // Act
        Either<ServiceError, Cliente> result = clienteService.createCliente(cliente);
        System.out.println(result);
        // Assert
        assertTrue(result.isLeft());
        assertTrue( result.getLeft() instanceof ServiceError.tarjetaCreditValidatorError);
    }

    @Test
    public void test_createCliente_withException() {
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjeta = new TarjetaCredito(UUID.randomUUID(), "1234567890123456", "John Doe", 1L, "12/25", LocalDateTime.now(), LocalDateTime.now(), false);
        Cliente cliente = new Cliente(usuario, List.of(tarjeta));

        when(userValidator.ValidateUser(any(Usuario.class))).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, Cliente> result = clienteService.createCliente(cliente);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.TarjetasLoadError);
    }

    // Successfully creates a user when validation passes
    @Test
    public void test_create_user_success() {
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(userValidator.ValidateUser(usuario)).thenReturn(Either.right(usuario));
        when(userRemoteRepository.createUser(usuario)).thenReturn(usuario);

        Either<ServiceError, Usuario> result = clienteService.createUser(usuario);

        assertTrue(result.isRight());
        assertEquals(usuario, result.get());
        verify(cacheUsuario).put(usuario.getId(), usuario);
    }

    // Handles validation errors by logging and sending a notification
    @Test
    public void test_create_user_validation_error() {
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());

        ServiceError validationError = new ServiceError.UservalidatorError("Error al validar el usuario");

        when(userValidator.ValidateUser(usuario)).thenReturn(Either.left(new UserErrors.NombreInvalido("Error al validar el usuario")));

        Either<ServiceError, Usuario> result = clienteService.createUser(usuario);

        assertTrue(result.isLeft());
        assertEquals(validationError.getMessage(), result.getLeft().getMessage());
    }

    @Test
    public void test_create_user_withException() {
        // Arrange
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());

        // Mockear el comportamiento de userValidator para lanzar una excepción
        when(userValidator.ValidateUser(any(Usuario.class))).thenThrow(new RuntimeException("Simulated exception"));

        // Act
        Either<ServiceError, Usuario> result = clienteService.createUser(usuario);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotCreated);
    }

    // Successfully creates a credit card when validation passes
    @Test
    public void test_create_tarjeta_success() {
        TarjetaCredito tarjeta = new TarjetaCredito(
                UUID.randomUUID(),
                "1234567890123456",
                "John Doe",
                1L,
                "12/25",
                LocalDateTime.now(),
                LocalDateTime.now(),
                false);


        when(tarjetaValidator.validarTarjetaCredito(tarjeta))
                .thenReturn(Either.right(tarjeta));
        when(creditCardRepository.create(tarjeta))
                .thenReturn(tarjeta);

        Either<ServiceError, TarjetaCredito> result = clienteService.createTarjeta(tarjeta);

        assertTrue(result.isRight());
        assertEquals(tarjeta, result.get());
        verify(cacheTarjeta).put(tarjeta.getId(), tarjeta);
    }

    // Handles validation errors by logging and sending error notifications
    @Test
    public void test_create_tarjeta_validation_error() {
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(1L)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(tarjetaValidator.validarTarjetaCredito(tarjetaCredito)).thenReturn(Either.left(new TarjetaErrors.NombreInvalido("Invalid card")));


        Either<ServiceError, TarjetaCredito> result = clienteService.createTarjeta(tarjetaCredito);
        System.out.println(result);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.tarjetaCreditValidatorError);
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    @Test
    public void test_create_tarjeta_withException() {
        // Arrange
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(1L)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // Mockear el comportamiento de tarjetaValidator para lanzar una excepción
        when(tarjetaValidator.validarTarjetaCredito(any(TarjetaCredito.class))).thenThrow(new RuntimeException("Simulated exception"));

        // Act
        Either<ServiceError, TarjetaCredito> result = clienteService.createTarjeta(tarjetaCredito);
        System.out.println(result);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.TarjeteNotCreated);
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    // Successfully updates a client when valid ID and client data are provided
    @Test
    public void test_update_cliente_success() {
        // Arrange
        Long clientId = 1L;
        Usuario usuario = new Usuario(clientId, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        List<TarjetaCredito> tarjetas = List.of(new TarjetaCredito(UUID.randomUUID(), "1234567890123456", "John Doe", clientId, "12/25", LocalDateTime.now(), LocalDateTime.now(), false));
        Cliente cliente = new Cliente(usuario, tarjetas);

        when(cacheTarjeta.buscarPorIdUsuario(clientId)).thenReturn(tarjetas);
        when(userRemoteRepository.getById(clientId)).thenReturn(usuario);
        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.right(usuario));
        when(userRemoteRepository.updateUser(anyLong(), any(Usuario.class))).thenReturn(usuario);
        when(tarjetaValidator.validarTarjetaCredito(any(TarjetaCredito.class))).thenReturn(Either.right(tarjetas.get(0)));

        // Act
        Either<ServiceError, Cliente> result = clienteService.updateCliente(clientId, cliente);
        System.out.println(result);
        // Assert
        assertTrue(result.isRight());
        assertEquals(cliente, result.get());
    }

    // Handles non-existent client ID by returning a UserNotFound error
    @Test
    public void test_update_cliente_non_existent_id() {
        // Arrange
        Long clientId = 999L;
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        Cliente cliente = new Cliente(usuario, new ArrayList<>());

        // Act
        Either<ServiceError, Cliente> result = clienteService.updateCliente(clientId, cliente);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotFound);
    }

    @Test
    public void test_update_cliente_user_validation_error() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);
        Cliente cliente = new Cliente(usuario, tarjetas);

        // Mockear dependencias
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);
        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.left(new UserErrors.NombreInvalido("Nombre inválido")));

        // Act
        Either<ServiceError, Cliente> result = clienteService.updateCliente(id, cliente);
        System.out.println(result);
        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotFound);

    }

    @Test
    public void test_update_cliente_credit_card_validation_error() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);
        Cliente cliente = new Cliente(usuario, tarjetas);

        // Mockear dependencias
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);
        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.right(usuario));
        when(userRemoteRepository.updateUser(anyLong(), any(Usuario.class))).thenReturn(usuario);
        when(tarjetaValidator.validarTarjetaCredito(any(TarjetaCredito.class))).thenReturn(Either.left(new TarjetaErrors.NombreInvalido("Nombre inválido")));

        // Act
        Either<ServiceError, Cliente> result = clienteService.updateCliente(id, cliente);
        System.out.println(result);
        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotFound);

    }

    @Test
    public void test_update_cliente_catchException() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);
        Cliente cliente = new Cliente(usuario, tarjetas);
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);
        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(any(Usuario.class))).thenReturn(Either.right(usuario));

        when(userRemoteRepository.updateUser(eq(id), any(Usuario.class))).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, Cliente> result = clienteService.updateCliente(id, cliente);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotUpdated);
    }

    @Test
    public void test_updateUser_success() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        Usuario usuarioActualizado = new Usuario(id, "Jane Doe", "janedoe", "jane@example.com", LocalDateTime.now(), LocalDateTime.now());

        // Mockear las dependencias necesarias
        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(usuarioActualizado)).thenReturn(Either.right(usuarioActualizado));
        when(userRemoteRepository.updateUser(id, usuarioActualizado)).thenReturn(usuarioActualizado);



        // Act
        Either<ServiceError, Usuario> result = clienteService.updateUser(id, usuarioActualizado);

        // Assert
        assertTrue(result.isRight());
        assertEquals(usuarioActualizado,result.get());
    }

    @Test
    public void test_updateUser_validationError() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());

        // Mockear las dependencias necesarias
        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(usuario)).thenReturn(Either.left(new UserErrors.NombreInvalido("Nombre inválido")));


        // Act
        Either<ServiceError, Usuario> result = clienteService.updateUser(id, usuario);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotFound);
    }

    @Test
    public void test_updateUser_withException() {
        // Arrange
        Long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(userRemoteRepository.getById(id)).thenReturn(usuario);
        when(userValidator.ValidateUser(usuario)).thenReturn(Either.right(usuario));
        when(userRemoteRepository.updateUser(eq(id), any(Usuario.class))).thenThrow(new RuntimeException("Simulated exception"));

        // Act
        Either<ServiceError, Usuario> result = clienteService.updateUser(id, usuario);
        System.out.println(result);
        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotUpdated);
    }

    @Test
    public void test_updateTarjeta_success() {
        // Arrange
        UUID id = UUID.randomUUID();
        Long clientId = 1L;
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(id)
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(clientId)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        TarjetaCredito tarjetaActualizada = TarjetaCredito.builder()
                .id(id)
                .numero("1234567890123456")
                .nombreTitular("John Doe Updated")
                .clientID(clientId)
                .fechaCaducidad("12/26")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(creditCardLocalRepository.findCreditCardById(id)).thenReturn(tarjetaCredito);
        when(tarjetaValidator.validarTarjetaCredito(tarjetaCredito)).thenReturn(Either.right(tarjetaCredito));
        when(creditCardRepository.update(id, tarjetaCredito)).thenReturn(tarjetaActualizada);

        // Act
        Either<ServiceError, TarjetaCredito> result = clienteService.updateTarjeta(id, tarjetaCredito);
        System.out.println(result);
        // Assert
        assertTrue(result.isRight());
        assertEquals(tarjetaActualizada, result.get());
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    @Test
    public void test_updateTarjeta_validationError() {
        // Arrange
        UUID id = UUID.randomUUID();
        Long clientId = 1L;
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(id)
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(clientId)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(creditCardLocalRepository.findCreditCardById(id)).thenReturn(tarjetaCredito);
        when(tarjetaValidator.validarTarjetaCredito(tarjetaCredito)).thenReturn(Either.left(new TarjetaErrors.NombreInvalido("Nombre inválido")));

        // Act
        Either<ServiceError, TarjetaCredito> result = clienteService.updateTarjeta(id, tarjetaCredito);
        System.out.println(result);
        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.TarjetasLoadError);
    }

    @Test
    public void test_updateTarjeta_withException() {
        // Arrange
        UUID id = UUID.randomUUID();
        Long clientId = 1L;
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(id)
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(clientId)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(creditCardLocalRepository.findCreditCardById(id)).thenReturn(tarjetaCredito);
        when(tarjetaValidator.validarTarjetaCredito(tarjetaCredito)).thenReturn(Either.right(tarjetaCredito));
        when(creditCardRepository.update(eq(id), any(TarjetaCredito.class))).thenThrow(new RuntimeException("Simulated exception"));

        // Act
        Either<ServiceError, TarjetaCredito> result = clienteService.updateTarjeta(id, tarjetaCredito);

        // Assert
        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.TarjeteNotDeleted);
    }

    @Test
    void loadClientesJson() {
    }

    @Test
    void saveClientesJson() {
    }

    @Test
    void loadUsersCsv() {
    }

    @Test
    void saveUsersCsv() {
    }

    @Test
    void loadTarjetasCsv() {
    }

    @Test
    void saveTarjetasCsv() {
    }
}