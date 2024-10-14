package org.example.usuarios.repository;

import org.example.exceptions.UserNotFoundException;
import org.example.mappers.UserMapper;
import org.example.models.Usuario;
import org.example.users.api.UserApiRest;
import org.example.users.api.createupdatedelete.Request;
import org.example.users.api.getAll.ResponseUserGetAll;
import org.example.users.api.getById.ResponseUserGetByid;
import org.example.users.repository.UserRemoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRemoteRepositoryTest {

    @Mock
    private UserApiRest userApiRest;

    @InjectMocks
    private UserRemoteRepository userRemoteRepository;

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
    void getByIdOK() {

        var response = ResponseUserGetByid.builder()
                .id(1)
                .name("Juan Perez")
                .username("juanp")
                .email("juan@example.com")
                .build();

        when(userApiRest.getById(1L)).thenReturn(CompletableFuture.completedFuture(response));

        var res = userRemoteRepository.getById(1L);

        assertEquals(1, res.getId());
        assertEquals("Juan Perez", res.getName());

        verify(userApiRest, times(1)).getById(1L);
    }

    @Test
    void getAllThrowsException() {
        when(userApiRest.getAll()).thenThrow(RuntimeException.class);


        assertThrows(RuntimeException.class, () -> userRemoteRepository.getAll());

        verify(userApiRest, times(1)).getAll();
    }


    @Test
    void getByIdNotFound() {


        when(userApiRest.getById(2L)).thenThrow(UserNotFoundException.class);

        var exception = assertThrows(UserNotFoundException.class, () -> userRemoteRepository.getById(2L));

        assertEquals(UserNotFoundException.class, exception.getClass());

        verify(userApiRest, times(1)).getById(2L);
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
    void updateUsuarioNotFound() {
        var usuario = Usuario.builder()
                .id(1L)
                .name("Juan")
                .username("juanp")
                .email("juan@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userApiRest.updateUser(eq(1L), any(Request.class))).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> userRemoteRepository.updateUser(1L, usuario));
        verify(userApiRest, times(1)).updateUser(eq(1L), any(Request.class));
    }

    @Test
    void deleteUsuarioOK() {
        when(userApiRest.deleteUser(1L)).thenReturn(CompletableFuture.completedFuture(null));

        userRemoteRepository.deleteById(1L);

        verify(userApiRest, times(1)).deleteUser(1L);
    }


    @Test
    void deleteUsuarioNotFound() {
        when(userApiRest.deleteUser(1L)).thenThrow(UserNotFoundException.class);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userRemoteRepository.deleteById(1L));

        assertEquals(UserNotFoundException.class, exception.getClass());

        verify(userApiRest, times(1)).deleteUser(1L);
    }




}