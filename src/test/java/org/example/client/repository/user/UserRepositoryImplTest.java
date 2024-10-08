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
        assertEquals(1l, result.size());
    }

    @Test
    void findUserById() {
        //act
        var result = usersRepository.findUserById(1l);
        //assert
        assertNotNull(result);
        assertEquals("Test", result.getName());
        assertEquals("TestUsername", result.getUsername());
    }

    @Test
    void saveUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUserById() {
    }

    @Test
    void deleteAllUsers() {
    }
}