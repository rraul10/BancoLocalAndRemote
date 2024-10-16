package org.example.client.service;

import io.vavr.control.Either;
import org.example.client.repository.creditcard.CreditCardLocalRepository;
import org.example.client.repository.user.UsersRepository;
import org.example.client.service.errors.ServiceError;
import org.example.creditcard.cache.CacheTarjetaImpl;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.creditcard.validator.TarjetaValidator;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.users.cache.CacheUsuario;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Usuario usuario;
    private TarjetaCredito tarjetaCredito;
    private Cliente cliente;

    @BeforeEach
    public void setup() {
        usuario = Usuario.builder()
                .id(1L)
                .name("John Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234-5678-9012-3456")
                .nombreTitular("John Doe")
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
        Cliente cliente = clientes.get(0);
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

        verify(userRemoteRepository).getAll();
        verify(creditCardRepository).getAll();
    }

    @Test
    void getAllUsers(){
        //TODO
    }

}