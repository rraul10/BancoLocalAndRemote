package org.example.client.repository.creditcard;

import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.user.UserRepositoryImpl;
import org.example.client.repository.user.UsersRepository;
import org.example.models.TarjetaCredito;
import org.junit.jupiter.api.*;
import org.junit.rules.Timeout;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class creditCardRepositoryImplTest {

    private static LocalDataBaseManager dataBaseManager;
    private static UsersRepository usersRepository;
    private static CreditCardRepository creditCardRepository;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        dataBaseManager = LocalDataBaseManager.getInstance();
        usersRepository = new UserRepositoryImpl(dataBaseManager);
        creditCardRepository = new CreditCardRepositoryImpl(dataBaseManager, usersRepository);
        dataBaseManager.connect();
        dataBaseManager.init("src/test/resources/initCliente.sql");
        creditCardRepository.saveCreditCard(TarjetaCredito.builder()
                .id(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"))
                        .numero("1234567890123456")
                        .nombreTitular("John Doe")
                        .clientID(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d7"))
                        .fechaCaducidad("12/24")
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                .build());
    }

    @AfterEach
    void tearDown() throws Exception {
        dataBaseManager.close();
    }

    @Test
    void findAllCreditCards() {
        //act
        var result = creditCardRepository.findAllCreditCards();
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findCreditCardById() {
    }

    @Test
    void findCreditCardByNumber() {
    }

    @Test
    void saveCreditCard() {
    }

    @Test
    void updateCreditCard() {
    }

    @Test
    void deleteCreditCard() {
    }

    @Test
    void deleteAllCreditCards() {
    }

    @Test
    void findAllCreditCardsByUserId() {
    }
}