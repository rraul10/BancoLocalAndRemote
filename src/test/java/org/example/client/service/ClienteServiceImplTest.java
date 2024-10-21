package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.dto.TarjetaCreditoDto;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.notification.Notification;
import org.example.notification.TarjetaNotificacion;
import org.example.notification.UserNotifications;
import org.example.service.ClienteServiceImpl;
import org.example.service.errors.ServiceError;
import org.example.users.cache.CacheUsuario;
import org.example.users.dto.UsuarioDto;
import org.example.users.repository.UserRemoteRepository;
import org.example.users.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        //verify(userNotifications).send(expectedNotification);
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

        when(usersRepository.findUserById(1L)).thenReturn(Optional.of(usuario));

        Either<ServiceError, Cliente> result = clienteService.deleteCliente(usuario.getId());

        assertTrue(result.isRight());
        assertEquals(cliente, result.get());


        Notification<TarjetaCreditoDto> expectedTarjetaNotificacion = new Notification<>(
                Notification.Type.DELETE,
                new TarjetaCreditoDto(tarjetaCredito),
                "Tarjeta eliminada con éxito " + tarjetaCredito.getNumero()
        );
        verify(tarjetaNotificacion).send(expectedTarjetaNotificacion);

        Notification<UsuarioDto> expectedClienteNotificacion = new Notification<>(
                Notification.Type.DELETE,
                new UsuarioDto(cliente.getUsuario()),
                "Cliente eliminado con éxito " + cliente.getUsuario().getId()
        );
        verify(userNotifications).send(expectedClienteNotificacion);

        verify(userRemoteRepository).deleteById(usuario.getId());
        verify(cacheUsuario).remove(usuario.getId());
        verify(creditCardRepository).delete(tarjetaCredito.getId());
        verify(cacheTarjeta).remove(tarjetaCredito.getId());

        verify(tarjetaNotificacion).send(any(Notification.class));
        verify(userNotifications).send(any(Notification.class));
    }







}