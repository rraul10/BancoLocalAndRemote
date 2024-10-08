package org.example.client.repository.user;

import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.creditcard.CreditCardRepositoryImpl;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private static LocalDataBaseManager dataBaseManager;
    private static UsersRepository usersRepository;

    @BeforeEach
    void setUp() throws SQLException {
        dataBaseManager = LocalDataBaseManager.getInstance();
        usersRepository = new UserRepositoryImpl(dataBaseManager);
        dataBaseManager.connect();
        dataBaseManager.initializeDatabase();
        usersRepository.saveUser(Usuario.builder()
                        .id(1l)
                        .name("Test")
                        .username("TestUsername")
                        .email("test@example.com")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                .build());
    }

    @AfterEach
    void tearDown() throws Exception {
        dataBaseManager.close();
    }

    @Test
    void findAllUsers() {
        //act
        var result = usersRepository.findAllUsers();
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findUsersByName() {
        //act
        var result = usersRepository.findUsersByName("Test");
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findUserById() {
        //act
        var result = usersRepository.findUserById(1L);
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(1L, result.getId()),
                ()-> assertEquals("Test", result.getName()),
                ()-> assertEquals("TestUsername", result.getUsername()),
                ()-> assertEquals("test@example.com", result.getEmail())
        );
    }

    @Test
    void saveUser() {
        //arrange
        Usuario userToSave = Usuario.builder()
                .id(2l)
                .name("Test2")
                .username("TestUsername2")
                .email("test2@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //act
        var savedUser = usersRepository.saveUser(userToSave);
        //assert
        assertAll(
                ()-> assertNotNull(savedUser),
                ()-> assertEquals(2L, savedUser.getId()),
                ()-> assertEquals("Test2", savedUser.getName()),
                ()-> assertEquals("TestUsername2", savedUser.getUsername()),
                ()-> assertEquals("test2@example.com", savedUser.getEmail())
        );

    }

    @Test
    void updateUser() {
        //arrange
        Usuario userToUpdate = Usuario.builder()
               .id(1L)
               .name("TestUpdated")
               .username("TestUsernameUpdated")
               .email("testUpdated@example.com")
               .createdAt(LocalDateTime.now())
               .updatedAt(LocalDateTime.now())
               .build();

        //act
        var updatedUser = usersRepository.updateUser(1L, userToUpdate);
        //assert
        assertAll(
                ()-> assertNotNull(updatedUser),
                ()-> assertEquals(1L, updatedUser.getId()),
                ()-> assertEquals("TestUpdated", updatedUser.getName()),
                ()-> assertEquals("TestUsernameUpdated", updatedUser.getUsername()),
                ()-> assertEquals("testUpdated@example.com", updatedUser.getEmail())
        );
    }

    @Test
    void deleteUserById() {
        //act
        var result = usersRepository.deleteUserById(1L);
        //assert
        assertTrue(result);
    }

    @Test
    void deleteAllUsers() {
        //act
        var result = usersRepository.deleteAllUsers();
        //assert
        assertTrue(result);
    }
}