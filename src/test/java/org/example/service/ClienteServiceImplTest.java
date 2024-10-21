package org.example.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.storage.StorageJsonClient;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.errors.TarjetaErrors;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.storage.StorageCsvCredCard;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.notification.Notification;
import org.example.notification.TarjetaNotificacion;
import org.example.notification.UserNotifications;
import org.example.service.errors.ServiceError;
import org.example.users.cache.CacheUsuario;
import org.example.users.dto.UsuarioDto;
import org.example.users.errors.UserErrors;
import org.example.users.repository.UserRemoteRepository;
import org.example.users.storage.StorageCsvUser;
import org.example.users.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Mock
    private  StorageJsonClient storageJsonClient;

    @Mock
    private  StorageCsvCredCard storageCsvCredCard;

    @Mock
    private  StorageCsvUser storageCsvUser;

    @InjectMocks
    private ClienteServiceImpl clienteService;



    private Usuario usuario;
    private TarjetaCredito tarjetaCredito;
    private Cliente cliente;


    @BeforeEach
    public void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .name("Juan")
                .username("juanete")
                .email("juanete@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234-5678-9012-3456")
                .nombreTitular("Juan Alberto")
                .clientID(1L)
                .fechaCaducidad("12/2025")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        cliente = new Cliente(usuario, List.of(tarjetaCredito));
    }


    @Test
    void getAllClientes() {
        when(usersRepository.findAllUsers()).thenReturn(List.of(usuario));
        when(creditCardLocalRepository.findAllCreditCards()).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<Cliente>> result = clienteService.getAllClientes(false);

        assertTrue(result.isRight());
        List<Cliente> clientes = result.get();
        assertEquals(1, clientes.size());
        Cliente cliente = clientes.getFirst();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());

        verify(usersRepository).findAllUsers();
        verify(creditCardLocalRepository).findAllCreditCards();

    }

    @Test
    void getAllClientesFromRemote() {
        when(userRemoteRepository.getAll()).thenReturn(List.of(usuario));
        when(creditCardRepository.getAll()).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<Cliente>> result = clienteService.getAllClientes(true);

        assertTrue(result.isRight());
        List<Cliente> clientes = result.get();
        assertEquals(1, clientes.size());
        Cliente cliente = clientes.get(0);
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());

        verify(userRemoteRepository, times(2)).getAll();
        verify(creditCardRepository,times(2)).getAll();
    }
    /**
     //Este test esta mal, falta que el repo lanze excepcion
     @Test
     void getAllClientesException() {
     when(usersRepository.findAllUsers()).thenThrow(SQLException.class);

     Either<ServiceError, List<Cliente>> result = clienteService.getAllClientes(false);

     assertTrue(result.isLeft());

     assertEquals(result.getLeft(), new ServiceError.ClienteLoadErrors("Error al obtener los clientes"));
     verify(usersRepository, times(1)).findAllUsers();
     }
     **/
    @Test
    void getAllUsers(){
        when(usersRepository.findAllUsers()).thenReturn(List.of(usuario));

        Either<ServiceError, List<Usuario>> result = clienteService.getAllUsers(false);

        assertTrue(result.isRight());
        List<Usuario> usuarios = result.get();
        assertEquals(1,usuarios.size());

        verify(usersRepository).findAllUsers();
    }

    @Test
    void getAllUsersFromRemote(){
        when(userRemoteRepository.getAll()).thenReturn(List.of(usuario));

        Either<ServiceError, List<Usuario>> result = clienteService.getAllUsers(true);

        assertTrue(result.isRight());
        List<Usuario> usuarios = result.get();
        assertEquals(1,usuarios.size());

        verify(userRemoteRepository, times(2)).getAll();
    }

    @Test
    void getAllUsersException() {
        when(usersRepository.findAllUsers()).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<Usuario>> result = clienteService.getAllUsers(false);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UsersLoadError);
        assertEquals("ERROR: Error al obtener los usuarios", error.getMessage());

        verify(usersRepository).findAllUsers();
    }

    @Test
    void getAllUsersFromRemoteException() {
        when(userRemoteRepository.getAll()).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<Usuario>> result = clienteService.getAllUsers(true);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UsersLoadError);
        assertEquals("ERROR: Error al obtener los usuarios", error.getMessage());

        verify(userRemoteRepository, times(2)).getAll();
    }



    @Test
    void getAllTarjetas() {
        when(creditCardLocalRepository.findAllCreditCards()).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<TarjetaCredito>> result = clienteService.getAllTarjetas(false);

        assertTrue(result.isRight());
        List<TarjetaCredito> tarjetas = result.get();
        assertEquals(1, tarjetas.size());

        verify(creditCardLocalRepository).findAllCreditCards();
    }

    @Test
    void getAllTarjetasFromRemoto() {
        when(creditCardRepository.getAll()).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<TarjetaCredito>> result = clienteService.getAllTarjetas(true);

        assertTrue(result.isRight());
        List<TarjetaCredito> tarjetas = result.get();
        assertEquals(1, tarjetas.size());

        verify(creditCardRepository,times(2)).getAll();
    }

    @Test
    void getAllTarjetasException() {
        when(creditCardLocalRepository.findAllCreditCards()).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<TarjetaCredito>> result = clienteService.getAllTarjetas(false);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Error al obtener los usuarios", error.getMessage());

        verify(creditCardLocalRepository).findAllCreditCards();
    }

    @Test
    void getAllTarjetasFromRemotoException() {
        when(creditCardRepository.getAll()).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<TarjetaCredito>> result = clienteService.getAllTarjetas(true);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Error al obtener los usuarios", error.getMessage());

        verify(creditCardRepository, times(2)).getAll();
    }


    @Test
    void getClienteByIdInCache() {
        when(cacheUsuario.containsKey(1L)).thenReturn(true);
        when(cacheUsuario.get(1L)).thenReturn(usuario);
        when(cacheTarjeta.buscarPorIdUsuario(1L)).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isRight());
        Cliente cliente = result.get();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());


        verify(usersRepository, never()).findUserById(anyLong());
        verify(userRemoteRepository, never()).getById(anyLong());
        verify(creditCardLocalRepository, never()).findAllCreditCardsByUserId(anyLong());
        verify(creditCardRepository, never()).findAllCreditCardsByUserId(anyLong());
    }

    @Test
    void getClienteByIdInRepo() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(1L)).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isRight());
        Cliente cliente = result.get();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());

        verify(cacheUsuario).put(usuario.getId(), usuario);

        verify(userRemoteRepository, never()).getById(anyLong());
    }

    @Test
    void getClienteByIdInRepoRemote() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.empty());
        when(userRemoteRepository.getById(1L)).thenReturn(usuario);
        when(cacheTarjeta.buscarPorIdUsuario(1L)).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isRight());
        Cliente cliente = result.get();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());

        verify(cacheUsuario).put(usuario.getId(), usuario);

        verify(usersRepository).findUserById(1L);
        verify(userRemoteRepository).getById(1L);
    }


    @Test
    void getClienteByIdEmpty() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.empty());
        when(userRemoteRepository.getById(1L)).thenReturn(null);

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.ClienteNotFound);
        assertEquals("ERROR: Cliente no encontrado con id: 1", error.getMessage());

        verify(usersRepository).findUserById(1L);
        verify(userRemoteRepository).getById(1L);
    }


    @Test
    void getClienteByIdTarjetaVaciaInRepo() {
        when(cacheUsuario.containsKey(1L)).thenReturn(true);
        when(cacheUsuario.get(1L)).thenReturn(usuario);
        when(cacheTarjeta.buscarPorIdUsuario(1L)).thenReturn(null);
        when(creditCardLocalRepository.findAllCreditCardsByUserId(1L)).thenReturn(null);
        when(creditCardRepository.findAllCreditCardsByUserId(1L)).thenReturn(null);

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isRight());
        Cliente cliente = result.get();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertTrue(cliente.getTarjetas().isEmpty());

        verify(cacheTarjeta).buscarPorIdUsuario(1L);
        verify(creditCardLocalRepository).findAllCreditCardsByUserId(1L);
        verify(creditCardRepository).findAllCreditCardsByUserId(1L);
    }


    @Test
    void getClienteByIdTarjetasInRemoteRepo() {
        when(cacheUsuario.containsKey(1L)).thenReturn(true);
        when(cacheUsuario.get(1L)).thenReturn(usuario);
        when(cacheTarjeta.buscarPorIdUsuario(1L)).thenReturn(null); // No hay tarjetas en caché
        when(creditCardLocalRepository.findAllCreditCardsByUserId(1L)).thenReturn(null); // No hay tarjetas locales
        when(creditCardRepository.findAllCreditCardsByUserId(1L)).thenReturn(List.of(tarjetaCredito)); // Tarjetas en repositorio remoto

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isRight());
        Cliente cliente = result.get();
        assertEquals(usuario.getId(), cliente.getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), cliente.getTarjetas().get(0).getClientID());

        verify(cacheTarjeta).buscarPorIdUsuario(1L);
        verify(creditCardLocalRepository).findAllCreditCardsByUserId(1L);
        verify(creditCardRepository).findAllCreditCardsByUserId(1L);
    }

    @Test
    void getClienteByIdException() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, Cliente> result = clienteService.getClienteById(1L);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.ClienteNotFound);
        assertEquals("ERROR: Error al obtener el cliente con id: 1", error.getMessage());

        verify(userRemoteRepository, never()).getById(anyLong());
    }

    @Test
    void getUserByIdUserInCache() {
        when(cacheUsuario.containsKey(1L)).thenReturn(true);
        when(cacheUsuario.get(1L)).thenReturn(usuario);

        Either<ServiceError, Usuario> result = clienteService.getUserById(1L);

        assertTrue(result.isRight());
        assertEquals(usuario.getId(), result.get().getId());

        verify(usersRepository, never()).findUserById(anyLong());
        verify(userRemoteRepository, never()).getById(anyLong());
    }

    @Test
    void getUserByIdUserInRepo() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.of(usuario));

        Either<ServiceError, Usuario> result = clienteService.getUserById(1L);

        assertTrue(result.isRight());
        assertEquals(usuario.getId(), result.get().getId());

        verify(cacheUsuario).put(usuario.getId(), usuario);
        verify(userRemoteRepository, never()).getById(anyLong());
    }

    @Test
    void getUserByIdUserInRemote() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.empty());
        when(userRemoteRepository.getById(1L)).thenReturn(usuario);

        Either<ServiceError, Usuario> result = clienteService.getUserById(1L);

        assertTrue(result.isRight());
        assertEquals(usuario.getId(), result.get().getId());

        verify(cacheUsuario).put(usuario.getId(), usuario);
    }

    @Test
    void getUserByIdUserNotFound() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenReturn(Optional.empty());
        when(userRemoteRepository.getById(1L)).thenReturn(null);

        Either<ServiceError, Usuario> result = clienteService.getUserById(1L);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UserNotFound);
        assertEquals("ERROR: Usuario no encontrado con id: 1", error.getMessage());

        verify(usersRepository).findUserById(1L);
        verify(userRemoteRepository).getById(1L);
    }

    @Test
    void getUserByIdException() {
        when(cacheUsuario.containsKey(1L)).thenReturn(false);
        when(usersRepository.findUserById(1L)).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, Usuario> result = clienteService.getUserById(1L);

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UserNotFound);
        assertEquals("ERROR: Error al obtener el usuario con id: 1", error.getMessage());

        verify(usersRepository).findUserById(1L);
    }


    @Test
    void getTarjetaByIdTarjetaInCache() {
        when(cacheTarjeta.containsKey(tarjetaCredito.getId())).thenReturn(true);
        when(cacheTarjeta.get(tarjetaCredito.getId())).thenReturn(tarjetaCredito);

        Either<ServiceError, TarjetaCredito> result = clienteService.getTarjetaById(tarjetaCredito.getId());

        assertTrue(result.isRight());
        assertEquals(tarjetaCredito.getId(), result.get().getId());

        verify(creditCardLocalRepository, never()).findCreditCardById(any(UUID.class));
        verify(creditCardRepository, never()).getById(any(UUID.class));
    }

    @Test
    void getTarjetaByIdTarjetaInLocalRepo() {
        when(cacheTarjeta.containsKey(tarjetaCredito.getId())).thenReturn(false);
        when(creditCardLocalRepository.findCreditCardById(tarjetaCredito.getId())).thenReturn(tarjetaCredito);

        Either<ServiceError, TarjetaCredito> result = clienteService.getTarjetaById(tarjetaCredito.getId());

        assertTrue(result.isRight());
        assertEquals(tarjetaCredito.getId(), result.get().getId());

        verify(cacheTarjeta).put(tarjetaCredito.getId(), tarjetaCredito);
        verify(creditCardRepository, never()).getById(any(UUID.class));
    }

    @Test
    void getTarjetaByIdTarjetaInRemoteRepo() {
        when(cacheTarjeta.containsKey(tarjetaCredito.getId())).thenReturn(false);
        when(creditCardLocalRepository.findCreditCardById(tarjetaCredito.getId())).thenReturn(null);
        when(creditCardRepository.getById(tarjetaCredito.getId())).thenReturn(Optional.of(tarjetaCredito));

        Either<ServiceError, TarjetaCredito> result = clienteService.getTarjetaById(tarjetaCredito.getId());

        assertTrue(result.isRight());
        assertEquals(tarjetaCredito.getId(), result.get().getId());

        verify(cacheTarjeta).put(tarjetaCredito.getId(), tarjetaCredito);
    }

    @Test
    void getTarjetaByIdTarjetaNotFound() {
        when(cacheTarjeta.containsKey(tarjetaCredito.getId())).thenReturn(false);
        when(creditCardLocalRepository.findCreditCardById(tarjetaCredito.getId())).thenReturn(null);
        when(creditCardRepository.getById(tarjetaCredito.getId())).thenReturn(Optional.empty());

        Either<ServiceError, TarjetaCredito> result = clienteService.getTarjetaById(tarjetaCredito.getId());

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Tarjeta no encontrada con id: " + tarjetaCredito.getId(), error.getMessage());

        verify(creditCardLocalRepository).findCreditCardById(tarjetaCredito.getId());
        verify(creditCardRepository).getById(tarjetaCredito.getId());
    }

    @Test
    void getTarjetaByIdException() {
        when(cacheTarjeta.containsKey(tarjetaCredito.getId())).thenReturn(false);
        when(creditCardLocalRepository.findCreditCardById(tarjetaCredito.getId())).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, TarjetaCredito> result = clienteService.getTarjetaById(tarjetaCredito.getId());

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Error al obtener la tarjeta con id: " + tarjetaCredito.getId(), error.getMessage());

        verify(creditCardLocalRepository).findCreditCardById(tarjetaCredito.getId());
    }

    @Test
    void getClienteByNameClientesInLocalRepo() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(List.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(usuario.getId())).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<Cliente>> result = clienteService.getClienteByName("Juan");

        assertTrue(result.isRight());
        List<Cliente> clientes = result.get();
        assertEquals(1, clientes.size());
        assertEquals(usuario.getId(), clientes.get(0).getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), clientes.get(0).getTarjetas().get(0).getClientID());

        verify(userRemoteRepository, never()).getByName(anyString());
        verify(creditCardLocalRepository, never()).findAllCreditCardsByUserId(anyLong());
        verify(creditCardRepository, never()).findAllCreditCardsByUserId(anyLong());
    }

    @Test
    void getClienteByNameClientesInRemoteRepo() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(null);
        when(userRemoteRepository.getByName("Juan")).thenReturn(List.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(usuario.getId())).thenReturn(List.of(tarjetaCredito));

        Either<ServiceError, List<Cliente>> result = clienteService.getClienteByName("Juan");

        assertTrue(result.isRight());
        List<Cliente> clientes = result.get();
        assertEquals(1, clientes.size());
        assertEquals(usuario.getId(), clientes.get(0).getUsuario().getId());
        assertEquals(tarjetaCredito.getClientID(), clientes.get(0).getTarjetas().get(0).getClientID());

        verify(usersRepository).findUsersByName("Juan");
        verify(creditCardLocalRepository, never()).findAllCreditCardsByUserId(anyLong());
        verify(creditCardRepository, never()).findAllCreditCardsByUserId(anyLong());
    }

    @Test
    void getClienteByNameClientesNotFound() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(null);
        when(userRemoteRepository.getByName("Juan")).thenReturn(null);

        Either<ServiceError, List<Cliente>> result = clienteService.getClienteByName("Juan");

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Cliente no encontrado con nombre: Juan", error.getMessage());

        verify(usersRepository).findUsersByName("Juan");
        verify(userRemoteRepository).getByName("Juan");
    }

    @Test
    void getClienteByNameException() {
        when(usersRepository.findUsersByName("Juan")).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<Cliente>> result = clienteService.getClienteByName("Juan");

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.TarjetasLoadError);
        assertEquals("ERROR: Error al obtener el cliente con id: Juan", error.getMessage());

        verify(usersRepository).findUsersByName("Juan");
    }

    @Test
    void getClienteByNameClientesSinTarjetas() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(List.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(usuario.getId())).thenReturn(null);
        when(creditCardLocalRepository.findAllCreditCardsByUserId(usuario.getId())).thenReturn(null);
        when(creditCardRepository.findAllCreditCardsByUserId(usuario.getId())).thenReturn(null);

        Either<ServiceError, List<Cliente>> result = clienteService.getClienteByName("Juan");

        assertTrue(result.isRight());
        List<Cliente> clientes = result.get();
        assertEquals(1, clientes.size());
        assertEquals(usuario.getId(), clientes.get(0).getUsuario().getId());
        assertTrue(clientes.get(0).getTarjetas().isEmpty());

        verify(usersRepository).findUsersByName("Juan");
        verify(creditCardLocalRepository).findAllCreditCardsByUserId(usuario.getId());
        verify(creditCardRepository).findAllCreditCardsByUserId(usuario.getId());
    }

    @Test
    void getUserByNameUsuariosInRepo() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(List.of(usuario));

        Either<ServiceError, List<Usuario>> result = clienteService.getUserByName("Juan");

        assertTrue(result.isRight());
        List<Usuario> usuarios = result.get();
        assertEquals(1, usuarios.size());
        assertEquals(usuario.getId(), usuarios.get(0).getId());

        verify(userRemoteRepository, never()).getByName(anyString());
    }

    @Test
    void getUserByNameUsuariosInRemoteRepo() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(null);
        when(userRemoteRepository.getByName("Juan")).thenReturn(List.of(usuario));

        Either<ServiceError, List<Usuario>> result = clienteService.getUserByName("Juan");

        assertTrue(result.isRight());
        List<Usuario> usuarios = result.get();
        assertEquals(1, usuarios.size());
        assertEquals(usuario.getId(), usuarios.get(0).getId());

        verify(usersRepository).findUsersByName("Juan");
        verify(userRemoteRepository).getByName("Juan");
    }

    @Test
    void getUserByNameUsuariosNotFound() {
        when(usersRepository.findUsersByName("Juan")).thenReturn(null);
        when(userRemoteRepository.getByName("Juan")).thenReturn(null);

        Either<ServiceError, List<Usuario>> result = clienteService.getUserByName("Juan");

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UserNotFound);
        assertEquals("ERROR: Usuario no encontrado con nombre: Juan", error.getMessage());

        verify(usersRepository).findUsersByName("Juan");
        verify(userRemoteRepository).getByName("Juan");
    }

    @Test
    void getUserByNameExceptionThrown() {
        when(usersRepository.findUsersByName("Juan")).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<Usuario>> result = clienteService.getUserByName("Juan");

        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertTrue(error instanceof ServiceError.UserNotFound);
        assertEquals("ERROR: Error al obtener el usuario con id: Juan", error.getMessage());

        verify(usersRepository).findUsersByName("Juan");
    }

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
    void loadDataSuccess() {
        when(userRemoteRepository.getAll()).thenReturn(List.of(usuario));
        when(creditCardRepository.getAll()).thenReturn(List.of(tarjetaCredito));

        clienteService.loadData();

        Notification<UsuarioDto> expectedNotification = new Notification<>(
                Notification.Type.REFRESH,
                null,
                "Datos remotos cargados correctamente"
        );

        userNotifications.send(expectedNotification);
        verify(usersRepository, times(2)).deleteAllUsers();
        verify(creditCardLocalRepository, times(2)).deleteAllCreditCards();
        verify(usersRepository).saveUser(usuario);
        verify(creditCardRepository).create(tarjetaCredito);
    }

    @Test
    void loadDataError() {
        when(userRemoteRepository.getAll()).thenThrow(new RuntimeException("Error en la conexión"));

        clienteService.loadData();

        Notification<UsuarioDto> expectedNotification = new Notification<>(
                Notification.Type.REFRESH,
                null,
                "Error al cargar los datos remotos: Error en la conexión"
        );

        userNotifications.send(expectedNotification);
        verify(usersRepository, times(2)).deleteAllUsers();
        verify(creditCardLocalRepository, times(2)).deleteAllCreditCards();
        verify(usersRepository, never()).saveUser(any());
        verify(creditCardRepository, never()).create(any());
    }


    @Test
    void deleteClienteSuccess() {
        Long id = 1L;
        Usuario usuario = new Usuario(id, "Juan", "juanete", "juanete@example.com", LocalDateTime.now(), LocalDateTime.now());

        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234-5678-9012")
                .nombreTitular("Juan")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);

        Cliente cliente = new Cliente(usuario, tarjetas);


        when(usersRepository.findUserById(id)).thenReturn(Optional.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);
        when(creditCardRepository.delete(tarjetaCredito.getId())).thenReturn(true);

        Either<ServiceError, Cliente> result = clienteService.deleteCliente(usuario.getId());

        assertTrue(result.isRight());
        assertEquals(cliente, result.get());


        verify(userRemoteRepository).deleteById(usuario.getId());
        verify(cacheUsuario).remove(usuario.getId());
        verify(creditCardRepository).delete(tarjetaCredito.getId());
        verify(cacheTarjeta).remove(tarjetaCredito.getId());
    }

    @Test
    void deleteClienteNotFound() {
        Long id = 1L;

        when(usersRepository.findUserById(id)).thenReturn(Optional.empty());

        Either<ServiceError, Cliente> result = clienteService.deleteCliente(id);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.UserNotFound);
        assertEquals("ERROR: Error al obtener el cliente con id: " + id, result.getLeft().getMessage());

        verify(userRemoteRepository, never()).deleteById(anyLong());
        verify(cacheUsuario, never()).remove(anyLong());
        verify(creditCardRepository, never()).delete(any(UUID.class));
        verify(cacheTarjeta, never()).remove(any(UUID.class));

    }

    @Test
    void deleteClienteErrorDeletingCard() {
        Long id = 1L;
        Usuario usuario = new Usuario(id, "Juan", "juanete", "juanete@example.com", LocalDateTime.now(), LocalDateTime.now());

        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234-5678-9012")
                .nombreTitular("Juan")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);

        Cliente cliente = new Cliente(usuario, tarjetas);

        when(usersRepository.findUserById(id)).thenReturn(Optional.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);

        doThrow(new RuntimeException("Error al eliminar la tarjeta")).when(creditCardRepository).delete(tarjetaCredito.getId());

        Either<ServiceError, Cliente> result = clienteService.deleteCliente(id);

        System.out.println(result);

        assertTrue(result.isLeft());

        verify(userRemoteRepository).deleteById(id);
        verify(cacheUsuario).remove(id);

        verify(creditCardRepository).delete(tarjetaCredito.getId());
    }

    @Test
    void deleteClienteGeneralException() {
        Long id = 1L;
        Usuario usuario = new Usuario(id, "Juan", "juanete", "juanete@example.com", LocalDateTime.now(), LocalDateTime.now());

        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234-5678-9012")
                .nombreTitular("Juan")
                .clientID(id)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> tarjetas = List.of(tarjetaCredito);

        Cliente cliente = new Cliente(usuario, tarjetas);

        when(usersRepository.findUserById(id)).thenReturn(Optional.of(usuario));
        when(cacheTarjeta.buscarPorIdUsuario(id)).thenReturn(tarjetas);

        doThrow(new RuntimeException("Error inesperado")).when(userRemoteRepository).deleteById(id);

        Either<ServiceError, Cliente> result = clienteService.deleteCliente(id);

        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al obtener el cliente con id: " + id, result.getLeft().getMessage());

        verify(userRemoteRepository).deleteById(id);
        verify(cacheUsuario, never()).remove(id);
    }

    @Test
    void deleteUserSuccess() {
        Long id = 1L;
        Usuario usuario = new Usuario(id, "Juan", "juanete", "juanete@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(usersRepository.findUserById(id)).thenReturn(Optional.of(usuario));

        Either<ServiceError, Usuario> result = clienteService.deleteUser(id);

        assertTrue(result.isRight());
        assertEquals(usuario, result.get());

        verify(userRemoteRepository).deleteById(id);
        verify(cacheUsuario).remove(id);
    }

    @Test
    void deleteUserNotFound() {
        Long id = 1L;

        when(usersRepository.findUserById(id)).thenReturn(Optional.empty());

        Either<ServiceError, Usuario> result = clienteService.deleteUser(id);

        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al obtener el usuario con id: 1", result.getLeft().getMessage());

        verify(userRemoteRepository, never()).deleteById(anyLong());
        verify(cacheUsuario, never()).remove(anyLong());
    }

    @Test
    void deleteUserRemoteDeleteError() {
        Long id = 1L;
        Usuario usuario = new Usuario(id, "Juan", "juanete", "juanete@example.com", LocalDateTime.now(), LocalDateTime.now());

        when(usersRepository.findUserById(id)).thenReturn(Optional.of(usuario));
        when(userRemoteRepository.deleteById(id)).thenThrow(new RuntimeException("Error de eliminación"));

        Either<ServiceError, Usuario> result = clienteService.deleteUser(id);

        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al obtener el usuario con id: 1", result.getLeft().getMessage());

        verify(userRemoteRepository).deleteById(id);
        verify(cacheUsuario, never()).remove(anyLong());
    }

    @Test
    void deleteUserGeneralError() {
        Long id = 1L;
        when(usersRepository.findUserById(id)).thenThrow(new RuntimeException("Error inesperado"));

        Either<ServiceError, Usuario> result = clienteService.deleteUser(id);

        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al obtener el usuario con id: 1", result.getLeft().getMessage());

        verify(userRemoteRepository, never()).deleteById(anyLong());
        verify(cacheUsuario, never()).remove(anyLong());
    }

    @Test
    void deleteTarjetaSuccess() {
        UUID id = UUID.randomUUID();
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(id)
                .numero("1234-5678-9012-3456")
                .nombreTitular("Juan Alberto")
                .clientID(1L)
                .fechaCaducidad("12/2025")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(creditCardRepository.getById(id)).thenReturn(Optional.of(tarjetaCredito));
        when(creditCardRepository.delete(id)).thenReturn(true);

        doNothing().when(cacheTarjeta).remove(id);

        clienteService.deleteTarjeta(id);

        verify(creditCardRepository).getById(id);
        verify(creditCardRepository).delete(id);
        verify(cacheTarjeta).remove(id);
    }

    @Test
    void deleteTarjetaNotFound() {
        UUID id = UUID.randomUUID();

        when(creditCardRepository.getById(id)).thenReturn(Optional.empty());

        Either<ServiceError, TarjetaCredito> result = clienteService.deleteTarjeta(id);

        assertTrue(result.isLeft(), "Se esperaba un error");
        assertEquals("ERROR: Error al obtener la tarjeta con id: " + id, result.getLeft().getMessage());

        verify(creditCardRepository, never()).delete(id);
        verify(cacheTarjeta, never()).remove(id);
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    @Test
    void deleteTarjetaDeleteError() {
        UUID id = UUID.randomUUID();
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(id)
                .numero("1234-5678-9012-3456")
                .nombreTitular("Juan Alberto")
                .clientID(1L)
                .fechaCaducidad("12/2025")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(creditCardRepository.getById(id)).thenReturn(Optional.of(tarjetaCredito));
        when(creditCardRepository.delete(id)).thenThrow(new RuntimeException("Error de base de datos"));

        Either<ServiceError, TarjetaCredito> result = clienteService.deleteTarjeta(id);

        assertTrue(result.isLeft(), "Se esperaba un error");
        assertEquals("ERROR: Error al obtener la tarjeta con id: " + id, result.getLeft().getMessage());

        verify(creditCardRepository).getById(id);
        verify(creditCardRepository).delete(id);
        verify(cacheTarjeta, never()).remove(id);
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    @Test
    void deleteTarjetaGeneralError() {
        UUID id = UUID.randomUUID();

        when(creditCardRepository.getById(id)).thenThrow(new RuntimeException("Error inesperado"));

        Either<ServiceError, TarjetaCredito> result = clienteService.deleteTarjeta(id);

        assertTrue(result.isLeft(), "Se esperaba un error");
        assertEquals("ERROR: Error al obtener la tarjeta con id: " + id, result.getLeft().getMessage());

        verify(creditCardRepository, never()).delete(id);
        verify(cacheTarjeta, never()).remove(id);
        verify(tarjetaNotificacion).send(any(Notification.class));
    }

    // Successfully loads a list of Cliente objects from a valid JSON file
    @Test
    public void importClientesJsonOnSuccess() {
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
        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());
        List<Cliente> expectedClientes = Arrays.asList(cliente);

        when(storageJsonClient.importList(file)).thenReturn(Flux.fromIterable(expectedClientes));

        Either<ServiceError, List<Cliente>> result = clienteService.loadClientesJson(file);

        assertTrue(result.isRight());
        assertEquals(expectedClientes, result.get());
    }

    @Test
    public void importClientesJsonOnError() {
        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());

        when(storageJsonClient.importList(file)).thenThrow(new RuntimeException("Simulated exception"));

        Either<ServiceError, List<Cliente>> result = clienteService.loadClientesJson(file);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.ClienteLoadErrors);
    }

    @Test
    public void saveClientesJsonOnSuccess() {
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
        List<Cliente> clientes = Arrays.asList(cliente);
        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());

        doNothing().when(storageJsonClient).exportList(clientes, file);

        Either<ServiceError, Void> result = clienteService.saveClientesJson(clientes, file);

        // Assert
        assertTrue(result.isRight());
    }

    @Test
    public void saveClientesJsonOnError() {
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
        List<Cliente> clientes = Arrays.asList(cliente);
        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());

        doThrow(new RuntimeException("Simulated exception")).when(storageJsonClient).exportList(clientes, file);

        Either<ServiceError, Void> result = clienteService.saveClientesJson(clientes, file);

        assertTrue(result.isLeft());
        assertTrue(result.getLeft() instanceof ServiceError.ImportErrors);
    }
    @Test
    void testLoadUsersCsv() {
        // Arrange
        File file = new File("usuarios.csv");
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(1L, "John Doe", "johndoe", "johndoe@example.com", LocalDateTime.now(), LocalDateTime.now()),
                new Usuario(2L, "Jane Doe", "janedoe", "janedoe@example.com", LocalDateTime.now(), LocalDateTime.now())
        );
        when(storageCsvUser.importList(file)).thenReturn(Flux.fromIterable(usuarios));

        // Act
        Either<ServiceError, List<Usuario>> result = clienteService.loadUsersCsv(file);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testLoadUsersCsvError() {
        // Arrange
        File file = new File("usuarios.csv");
        when(storageCsvUser.importList(file)).thenThrow(new RuntimeException("Error al cargar los usuarios"));

        // Act
        Either<ServiceError, List<Usuario>> result = clienteService.loadUsersCsv(file);

        // Assert
        assertNotNull(result);
        assertTrue(result.isLeft());
    }

    @Test
    void testSaveUsersSuccess() {
        // Arrange
        Long id = 1L;
        String name = "John Doe";
        String username = "johndoe";
        String email = "johndoe@example.com";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        List<Usuario> usuarios = Arrays.asList(
                new Usuario(id, name, username, email, createdAt, updatedAt),
                new Usuario(2L, "Jane Doe", "janedoe", "janedoe@example.com", createdAt, updatedAt)
        );
        File file = new File("usuarios.csv");
        doThrow(new RuntimeException("Error al guardar los usuarios")).when(storageCsvUser).exportList(usuarios, file);

        // Act
        Either<ServiceError, Void> result = clienteService.saveUsersCsv(usuarios, file);

        // Assert
        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al guardar los usuarios", result.getLeft().getMessage());
    }

    @Test
    void testSaveUsersError() {
        // Arrange
        Long id = 1L;
        String name = "John Doe";
        String username = "johndoe";
        String email = "johndoe@example.com";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<Usuario> usuarios = Arrays.asList(
                new Usuario(id, name, username, email, createdAt, updatedAt),
                new Usuario(2L, "Jane Doe", "janedoe", "janedoe@example.com", createdAt, updatedAt)
        );
        File file = new File("usuarios.csv");
        doThrow(new RuntimeException("Error al guardar los usuarios")).when(storageCsvUser).exportList(usuarios, file);
        // Act
        Either<ServiceError, Void> result = clienteService.saveUsersCsv(usuarios, file);
        // Assert
        assertTrue(result.isLeft());
    }

    @Test
    void testLoadTarjetasCsvSuccess() {
        // Arrange
        List<TarjetaCredito> tarjetas = Arrays.asList(
                new TarjetaCredito(UUID.randomUUID(), "123456", "Juan", 1L, "12/2025", LocalDateTime.now(), LocalDateTime.now(),true),
                new TarjetaCredito(UUID.randomUUID(), "789012", "Maria", 2L, "06/2026", LocalDateTime.now(), LocalDateTime.now(), true)
        );
        File file = new File("tarjetas.csv");
        when(storageCsvCredCard.importList(file)).thenReturn(Flux.fromIterable(tarjetas));

        // Act
        Either<ServiceError, List<TarjetaCredito>> result = clienteService.loadTarjetasCsv(file);

        // Assert
        assertTrue(result.isRight());
        assertEquals(result.get(), tarjetas);
        verify(storageCsvCredCard, times(1)).importList(file);
    }

    @Test
    void testLoadTarjetasCsvError() {
        // Arrange
        File file = new File("tarjetas.csv");
        when(storageCsvCredCard.importList(file)).thenReturn(Flux.error(new RuntimeException("Error al leer el archivo")));


        // Act
        Either<ServiceError, List<TarjetaCredito>> result = clienteService.loadTarjetasCsv(file);

        // Assert
        assertTrue(result.isLeft());
        ServiceError error = result.getLeft();
        assertEquals("ERROR: Error al cargar las tarjetas", error.getMessage());
    }

    @Test
    void testSaveTarjetasCsvSuccess() {
        // Arrange
        List<TarjetaCredito> tarjetasCredito = Arrays.asList(new TarjetaCredito(), new TarjetaCredito());
        File file = new File("tarjetas.csv");
        doNothing().when(storageCsvCredCard).exportList(tarjetasCredito, file);

        // Act
        Either<ServiceError, Void> result = clienteService.saveTarjetasCsv(tarjetasCredito, file);

        // Assert
        assertTrue(result.isRight());
        assertNull(result.get());

    }

    @Test
    void testSaveTarjetasCsvError() {
        List<TarjetaCredito> tarjetasCredito = Arrays.asList(new TarjetaCredito(), new TarjetaCredito());
        File file = new File("tarjetas.csv");
        doThrow(new RuntimeException("Error al guardar las tarjetas")).when(storageCsvCredCard).exportList(tarjetasCredito, file);

        // Act
        Either<ServiceError, Void> result = clienteService.saveTarjetasCsv(tarjetasCredito, file);

        // Assert
        assertTrue(result.isLeft());
        assertEquals("ERROR: Error al guardar las tarjetas", result.getLeft().getMessage());
    }
}