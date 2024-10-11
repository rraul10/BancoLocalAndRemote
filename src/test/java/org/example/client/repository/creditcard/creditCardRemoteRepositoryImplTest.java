package org.example.client.repository.creditcard;

import org.example.client.database.LocalDataBaseManager;
import org.example.client.repository.user.UserLocalRepositoryImpl;
import org.example.client.repository.user.UsersRepository;
import org.example.config.ConfigProperties;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
class creditCardRemoteRepositoryImplTest {

    private static LocalDataBaseManager dataBaseManager;
    private static UsersRepository usersRepository;
    private static CreditCardLocalRepository creditCardLocalRepository;

    @BeforeAll
     static void setUpAll() throws SQLException {
        ConfigProperties properties = new ConfigProperties("application.properties");
        dataBaseManager = LocalDataBaseManager.getInstanceMemory(properties);
        dataBaseManager.connect();
        dataBaseManager.initializeDatabase();
        usersRepository = new UserLocalRepositoryImpl(dataBaseManager);
        creditCardLocalRepository = new CreditCardLocalRepositoryImpl(dataBaseManager, usersRepository);

    }

    @BeforeEach
    void setUp() {
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        creditCardLocalRepository.saveCreditCard(TarjetaCredito.builder()
                .id(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"))
                .numero("1234567890123456")
                .nombreTitular("Test")
                .clientID(1L)
                .fechaCaducidad("12/24")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

    }

    @AfterEach
    void tearDown() {
        creditCardLocalRepository.deleteAllCreditCards();
        usersRepository.deleteAllUsers();
    }



    @Test
    void findAllCreditCards() {
        creditCardLocalRepository.saveCreditCard(TarjetaCredito.builder()
                .id(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"))
                .numero("1234567890123456")
                .nombreTitular("Test")
                .clientID(1L)
                .fechaCaducidad("12/24")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        usersRepository.saveUser(Usuario.builder()
                .id(1L)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = creditCardLocalRepository.findAllCreditCards();
        //assert
        assertEquals(1, result.size());
    }

    @Test
    void findCreditCardById() {

        //act
        var result = creditCardLocalRepository.findCreditCardById(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"));
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"), result.getId()),
                ()-> assertEquals("1234567890123456", result.getNumero()),
                ()-> assertEquals("Test", result.getNombreTitular()),
                ()-> assertEquals(1L, result.getClientID()),
                ()-> assertEquals("12/24", result.getFechaCaducidad()),
                ()-> assertFalse(result.getIsDeleted())
        );
    }

    @Test
    void findCreditCardByIdNotFound() {
        //act
        var result = creditCardLocalRepository.findCreditCardById(UUID.fromString("3a62d823-e068-4560-a464-9daa364e03d9"));
        //assert
        assertNull(result);
    }

    @Test
    void findCreditCardByNumber() {

        //act
        var result = creditCardLocalRepository.findCreditCardByNumber("1234567890123456");
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"), result.getId()),
                ()-> assertEquals("1234567890123456", result.getNumero()),
                ()-> assertEquals("Test", result.getNombreTitular()),
                ()-> assertEquals(1L, result.getClientID()),
                ()-> assertEquals("12/24", result.getFechaCaducidad()),
                ()-> assertFalse(result.getIsDeleted())
        );
    }

    @Test
    void findCreditCardByNumberNotFound() {
        //act
        var result = creditCardLocalRepository.findCreditCardByNumber("1234567234890123457");
        //assert
        assertNull(result);
    }

    @Test
    void saveCreditCard() {

        //act
        var tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d9"))
                        .numero("1234567890123456")
                        .nombreTitular("TestSave")
                        .clientID(1L)
                        .fechaCaducidad("11/24")
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                .build();
        var result = creditCardLocalRepository.saveCreditCard(tarjetaCredito);
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d9"), result.getId()),
                ()-> assertEquals("1234567890123456", result.getNumero()),
                ()-> assertEquals("TestSave", result.getNombreTitular()),
                ()-> assertEquals(1L, result.getClientID()),
                ()-> assertEquals("11/24", result.getFechaCaducidad()),
                ()-> assertFalse(result.getIsDeleted())
        );
    }

    @Test
    void updateCreditCard() {

        //arrange
        TarjetaCredito tarjetaCreditoUpdated = creditCardLocalRepository.findCreditCardById(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"));
        tarjetaCreditoUpdated.setFechaCaducidad("11/24");
        var localDateTime = LocalDateTime.now();
        tarjetaCreditoUpdated.setUpdatedAt(localDateTime);
        //act
        var result = creditCardLocalRepository.updateCreditCard(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"),tarjetaCreditoUpdated);
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"), result.getId()),
                ()-> assertEquals("1234567890123456", result.getNumero()),
                ()-> assertEquals("Test", result.getNombreTitular()),
                ()-> assertEquals(1L, result.getClientID()),
                ()-> assertEquals("11/24", result.getFechaCaducidad()),
                ()-> assertEquals(localDateTime, result.getUpdatedAt()),
                ()-> assertFalse(result.getIsDeleted())
        );
    }

    @Test
    void updateCreditCardNotFound() {
        var localDateTime = LocalDateTime.now();
        TarjetaCredito tarjetaCreditoNotFound = TarjetaCredito.builder()
                .id(UUID.fromString("3a41d823-e068-4560-a464-9daa369e03d6"))
                .numero("1234567890123456")
                .nombreTitular("Test")
                .clientID(1L)
                .fechaCaducidad("12/24")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        //arrange
        tarjetaCreditoNotFound.setFechaCaducidad("11/24");
        tarjetaCreditoNotFound.setUpdatedAt(localDateTime);
        //act
        var result = creditCardLocalRepository.updateCreditCard(UUID.fromString("3a41d823-e068-4560-a464-9daa369e03d6"),tarjetaCreditoNotFound);
        //assert
        assertNull(result);
    }

    @Test
    void deleteCreditCard() {

        //act
        var result = creditCardLocalRepository.deleteCreditCard(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"));
        //assert
        assertTrue(result);
    }

    @Test
    void deleteCreditCardNotFound() {
        //act
        var result = creditCardLocalRepository.deleteCreditCard(UUID.fromString("3a41d823-e068-4560-a464-9daa369e03d6"));
        //assert
        assertFalse(result);
    }

    @Test
    void deleteAllCreditCards() {

        //act
        var result = creditCardLocalRepository.deleteAllCreditCards();
        //assert
        assertTrue(result);
    }

    @Test
    void findAllCreditCardsByUserId() {
        creditCardLocalRepository.saveCreditCard(TarjetaCredito.builder()
                .id(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"))
                .numero("1234567890123456")
                .nombreTitular("Test")
                .clientID(1L)
                .fechaCaducidad("12/24")
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        usersRepository.saveUser(Usuario.builder()
                .id(1l)
                .name("Test")
                .username("TestUsername")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        //act
        var result = creditCardLocalRepository.findAllCreditCardsByUserId(1L);
        //assert
        assertAll(
                ()-> assertNotNull(result),
                ()-> assertEquals(1, result.size()),
                ()-> assertEquals(UUID.fromString("3a62d823-e068-4560-a464-9daa369e03d6"), result.get(0).getId()),
                ()-> assertEquals("1234567890123456", result.get(0).getNumero()),
                ()-> assertEquals("Test", result.get(0).getNombreTitular()),
                ()-> assertEquals(1L, result.get(0).getClientID()),
                ()-> assertEquals("12/24", result.get(0).getFechaCaducidad()),
                ()-> assertFalse(result.get(0).getIsDeleted())
        );;
    }
    @Test
    void findAllCreditCardsByUserIdNotFound() {
        //act
        var result = creditCardLocalRepository.findAllCreditCardsByUserId(4L);
        //assert
        assertEquals(0, result.size());
    }
}