package org.example.client.repository.user;

import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.creditcard.CreditCardRepositoryImpl;
import org.example.config.ConfigProperties;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private static LocalDataBaseManager dataBaseManager;
    private static UsersRepository usersRepository;

    @BeforeAll
    static void setUpAll() throws SQLException {
        ConfigProperties config = new ConfigProperties("application.properties");
        // Inicializa el pool de conexiones
        dataBaseManager = LocalDataBaseManager.getInstanceMemory(config);
        usersRepository = new UserRepositoryImpl(dataBaseManager);
        dataBaseManager.connect();
        dataBaseManager.initializeDatabase();
    }



    @Test
    void findAllUsers() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.findAllUsers();
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findUsersByName() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.findUsersByName("Test");
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findUsersByNameNotFound(){
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.findUsersByName("NotFound");
        //assert
        assertEquals(0, result.size());
    }

    @Test
    void findUserById() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.findUserById(1L);
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(1L, result.get().getId()),
                ()-> assertEquals("Test", result.get().getName()),
                ()-> assertEquals("TestUsername", result.get().getUsername()),
                ()-> assertEquals("test@example.com", result.get().getEmail())
        );
    }

    @Test
    void findUserByIdNotFound(){
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.findUserById(99L);
        //assert
        assertEquals(Optional.empty(), result);
    }

    @Test
    void saveUser() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
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
                ()-> assertEquals(2L, savedUser.get().getId()),
                ()-> assertEquals("Test2", savedUser.get().getName()),
                ()-> assertEquals("TestUsername2", savedUser.get().getUsername()),
                ()-> assertEquals("test2@example.com", savedUser.get().getEmail())
        );

    }

    @Test
    void updateUser() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
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
                ()-> assertEquals(1L, updatedUser.get().getId()),
                ()-> assertEquals("TestUpdated", updatedUser.get().getName()),
                ()-> assertEquals("TestUsernameUpdated", updatedUser.get().getUsername()),
                ()-> assertEquals("testUpdated@example.com", updatedUser.get().getEmail())
        );
    }

    @Test
    void updateUserNotFound(){
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //arrange
        Usuario userToUpdate = Usuario.builder()
                .id(99L)
                .name("TestUpdated")
                .username("TestUsernameUpdated")
                .email("testUpdated@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        //act
        var updatedUser = usersRepository.updateUser(99L, userToUpdate);
        //assert
        assertEquals(Optional.empty(), updatedUser);
    }

    @Test
    void deleteUserById() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.deleteUserById(1L);
        //assert
        assertTrue(result);
    }

    @Test
    void deleteUserByIdNotFound(){
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.deleteUserById(99L);
        //assert
        assertFalse(result);
    }

    @Test
    void deleteAllUsers() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = usersRepository.deleteAllUsers();
        //assert
        assertTrue(result);
    }
}