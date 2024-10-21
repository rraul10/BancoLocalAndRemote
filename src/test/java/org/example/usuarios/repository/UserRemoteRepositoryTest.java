package org.example.usuarios.repository;

import org.example.exceptions.UserNotFoundException;
import org.example.mappers.UserMapper;
import org.example.models.Usuario;
import org.example.users.api.UserApiRest;
import org.example.users.api.createupdatedelete.Request;
import org.example.users.api.createupdatedelete.Response;
import org.example.users.api.getAll.ResponseUserGetAll;
import org.example.users.api.getById.ResponseUserGetByid;
import org.example.users.api.getByName.ResponseUserGetByName;
import org.example.users.repository.UserRemoteRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRemoteRepositoryTest {

    @Mock
    private UserApiRest userApiRest;

    @InjectMocks
    private UserRemoteRepositoryImpl userRemoteRepository;

    @Test
    void getAll() {
        var user1 = ResponseUserGetAll.builder()
                .id(1)
                .name("Juan Perez")
                .username("juanp")
                .email("juan@example.com")
                .build();

        var user2 = ResponseUserGetAll.builder()
                .id(2)
                .name("Ana Gomez")
                .username("anag")
                .email("ana@example.com")
                .build();

        when(userApiRest.getAll()).thenReturn(CompletableFuture.completedFuture(List.of(user1, user2)));

        List<Usuario> usuarios = userRemoteRepository.getAll();

        assertEquals(2, usuarios.size());
        assertEquals("Juan Perez", usuarios.get(0).getName());
        assertEquals("Ana Gomez", usuarios.get(1).getName());

        verify(userApiRest, times(1)).getAll();
    }

    @Test
    void getAll_withException() {
        // Arrange
        CompletableFuture<List<ResponseUserGetAll>> call = mock(CompletableFuture.class);
        when(userApiRest.getAll()).thenReturn(call);

        // Simular una excepción cuando se llama a call.get()
        try {
            when(call.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        // Instanciar el repositorio con sus dependencias
        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        List<Usuario> result = userRemoteRepository.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Debería estar vacío porque se maneja la excepción y se devuelve una lista vacía
        verify(userApiRest, times(1)).getAll();
    }


    @Test
    void getByIdOK() {

        var response = ResponseUserGetByid.builder()
                .id(1)
                .name("Juan Perez")
                .username("juanp")
                .email("juan@example.com")
                .build();

        when(userApiRest.getById(1L)).thenReturn(CompletableFuture.completedFuture(response));

        var res = userRemoteRepository.getById(1);

        assertEquals(1, res.getId());
        assertEquals("Juan Perez", res.getName());

        verify(userApiRest, times(1)).getById(1L);
    }

        @Test
    void getById_withException() {
        // Arrange
        long id = 1L;
        CompletableFuture<ResponseUserGetByid> call = mock(CompletableFuture.class);
        when(userApiRest.getById(id)).thenReturn(call);

        try {
            when(call.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        Usuario result = userRemoteRepository.getById(id);

        // Assert
        assertNull(result); // Debería ser null porque la excepción fue manejada y se devolvió null
    }

        @Test
    void findByNameOK() {
        var user1 = ResponseUserGetByName.builder()
                .id(1)
                .name("Juan Perez")
                .username("juanp")
                .email("juan@example.com")
                .build();

        when(userApiRest.getByName("Juan Perez")).thenReturn(CompletableFuture.completedFuture(List.of(user1)));
        var res = userRemoteRepository.getByName("Juan Perez");

        assertEquals(1, res.size());
        assertEquals("Juan Perez", res.get(0).getName());

        verify(userApiRest, times(1)).getByName("Juan Perez");
    }

    @Test
    void getByName_withException() {
        // Arrange
        String name = "Juan";
        CompletableFuture<List<ResponseUserGetByName>> call = mock(CompletableFuture.class);
        when(userApiRest.getByName(name)).thenReturn(call);

        try {
            when(call.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        List<Usuario> result = userRemoteRepository.getByName(name);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createUsuarioOK() {


        var usuario = Usuario.builder()
                .id(1L)
                .name("Juan Perez")
                .username("juanp")
                .email("juan@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userApiRest.createUser(any(Request.class))).thenReturn(CompletableFuture.completedFuture(UserMapper.toResponse(usuario)));

        var res = userRemoteRepository.createUser(usuario);

        assertEquals(1L, res.getId());
        assertEquals("Juan Perez", res.getName());

        verify(userApiRest, times(1)).createUser(any(Request.class));
    }

    @Test
    void createUser_withException() {
        // Arrange
        Usuario usuario = new Usuario(1L, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        CompletableFuture<Response> callSync = mock(CompletableFuture.class);
        when(userApiRest.createUser(any())).thenReturn(callSync);

        try {
            when(callSync.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        Usuario result = userRemoteRepository.createUser(usuario);

        // Assert
        assertNull(result); // Debería ser null porque la excepción fue manejada y se devolvió null
        verify(userApiRest).createUser(any());
    }


    @Test
    void updateUsuarioOK() {

        var usuario = Usuario.builder()
                .id(1L)
                .name("Juan")
                .username("juanp")
                .email("juan@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        when(userApiRest.updateUser(eq(1L), any(Request.class)))
                .thenReturn(CompletableFuture.completedFuture(UserMapper.toResponse(usuario)));

        var res = userRemoteRepository.updateUser(1L, usuario);

        assertEquals(1L, res.getId());
        assertEquals("Juan", res.getName());
        assertEquals("juanp", res.getUsername());
        assertEquals("juan@example.com", res.getEmail());

        verify(userApiRest, times(1)).updateUser(eq(1L), any(Request.class));
    }

    @Test
    void updateUser_withException() {
        // Arrange
        long id = 1L;
        Usuario usuario = new Usuario(id, "John Doe", "johndoe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        CompletableFuture<Response> callSync = mock(CompletableFuture.class);
        when(userApiRest.updateUser(eq(id), any())).thenReturn(callSync);

        try {
            when(callSync.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        Usuario result = userRemoteRepository.updateUser(id, usuario);

        // Assert
        assertNull(result); // Debería ser null porque la excepción fue manejada y se devolvió null
        verify(userApiRest).updateUser(eq(id), any());
    }


    @Test
    void deleteUsuarioOK() {
        when(userApiRest.deleteUser(1L)).thenReturn(CompletableFuture.completedFuture(null));

        userRemoteRepository.deleteById(1L);

        verify(userApiRest, times(1)).deleteUser(1L);
    }

    @Test
    void deleteById_withException() {
        // Arrange
        long id = 1L;
        CompletableFuture<Response> callSync = mock(CompletableFuture.class);
        when(userApiRest.deleteUser(id)).thenReturn(callSync);

        try {
            when(callSync.get()).thenThrow(new ExecutionException(new Exception("Simulated exception")));
        } catch (InterruptedException | ExecutionException e) {
            fail("Exception during test setup");
        }

        UserRemoteRepositoryImpl userRemoteRepository = new UserRemoteRepositoryImpl(userApiRest);

        // Act
        Usuario result = userRemoteRepository.deleteById(id);

        // Assert
        assertNull(result); // Debería ser null porque la excepción fue manejada y se devolvió null
        verify(userApiRest).deleteUser(id);
    }





}