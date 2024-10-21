package org.example.creditcard.repositories;

import org.example.creditcard.database.DataBaseManager;

import java.sql.*;
import org.example.models.TarjetaCredito;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class CreditCardRepositoryImplTest {

    @Mock
    DataBaseManager dataBaseManager;

    @Mock
    Connection connection;

    @Mock
    PreparedStatement statement;

    @Mock
    ResultSet resultSet;

    @InjectMocks
    CreditCardRepositoryImpl repository;

    @Test
    public void getAll() {
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {

            UUID id = UUID.randomUUID();

            when(dataBaseManager.connect()).thenReturn(connection);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, false);

            // Asegúrate de que los nombres de los campos coinciden con los utilizados en tu función
            when(resultSet.getObject("id", UUID.class)).thenReturn(id);
            when(resultSet.getString("numero")).thenReturn("1234567890123456");
            when(resultSet.getString("nombreTitular")).thenReturn("John Doe");
            when(resultSet.getObject("clienteID", String.class)).thenReturn("1");
            when(resultSet.getString("fechaCaducidad")).thenReturn("12/99");
            when(resultSet.getObject("createdAt", LocalDateTime.class)).thenReturn(LocalDateTime.now());
            when(resultSet.getObject("updatedAt", LocalDateTime.class)).thenReturn(LocalDateTime.now());
            when(resultSet.getObject("isDeleted", Boolean.class)).thenReturn(false);

            List<TarjetaCredito> tarjetas = repository.getAll();
            assertNotNull(tarjetas);
            assertFalse(tarjetas.isEmpty());
        } catch (SQLException e) {
            fail("SQL Exception should not occur");
        }
    }


    @Test
    public void getAllFails() {


        try {
            when(dataBaseManager.connect()).thenThrow(new SQLException("Connection failed"));
        } catch (SQLException e) {
            fail("Mock setup failed");
        }

        List<TarjetaCredito> tarjetas = repository.getAll();

        assertNotNull(tarjetas);
        assertTrue(tarjetas.isEmpty());
    }

    @Test
    public void getById() {
        UUID validId = UUID.randomUUID();
        TarjetaCredito expectedCard = TarjetaCredito.builder()
                .id(validId)
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(2L)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();


        try {
            when(dataBaseManager.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            doReturn(expectedCard.getId()).when(resultSet).getObject("id", UUID.class);
            doReturn(expectedCard.getNumero()).when(resultSet).getString("numero");
            doReturn(expectedCard.getNombreTitular()).when(resultSet).getString("nombreTitular");
            doReturn(expectedCard.getClientID()).when(resultSet).getLong("clientID");
            doReturn(expectedCard.getFechaCaducidad()).when(resultSet).getString("fechaCaducidad");
            doReturn(expectedCard.getCreatedAt()).when(resultSet).getObject("createdAt", LocalDateTime.class);
            doReturn(expectedCard.getUpdatedAt()).when(resultSet).getObject("updatedAt", LocalDateTime.class);
            doReturn(expectedCard.getIsDeleted()).when(resultSet).getObject("isDeleted", Boolean.class);

            Optional<TarjetaCredito> result = repository.getById(validId);

            assertTrue(result.isPresent());
            assertEquals(expectedCard, result.get());
        } catch (SQLException e) {
            fail("SQLException was thrown");
        }
    }


    @Test
    public void getByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        try {
            when(dataBaseManager.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false);

            Optional<TarjetaCredito> result = repository.getById(nonExistentId);

            assertFalse(result.isPresent());
        } catch (SQLException e) {
            fail("SQL Exception should not occur");
        }
    }


    @Test
    public void getByIdFails() {
        UUID anyId = UUID.randomUUID();


        try {
            when(dataBaseManager.connect()).thenThrow(new SQLException("Connection failed"));

            Optional<TarjetaCredito> result = repository.getById(anyId);

            assertFalse(result.isPresent());
        } catch (SQLException e) {
            fail("SQLException should be handled within the method");
        }
    }


    @Test
    public void create() throws SQLException {

        TarjetaCredito creditCard = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(dataBaseManager.connect()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(statement);
        when(statement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn(UUID.randomUUID().toString());

        TarjetaCredito result = repository.create(creditCard);


        assertNotNull(result.getId());
        assertEquals(creditCard.getNumero(), result.getNumero());
        assertEquals(creditCard.getNombreTitular(), result.getNombreTitular());
    }


    @Test
    public void createFails() throws SQLException {

        TarjetaCredito creditCard = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(3).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(dataBaseManager.connect()).thenThrow(new SQLException("Connection failed"));

        TarjetaCredito result = repository.create(creditCard);


        assertEquals(creditCard, result);
    }


    @Test
    public void update() throws SQLException {
        UUID id = UUID.randomUUID();
        TarjetaCredito creditcard = TarjetaCredito.builder()
                .id(id)
                .nombreTitular("John Doe")
                .numero("1234567890123456")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(dataBaseManager.connect()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        TarjetaCredito updatedCard = repository.update(id, creditcard);

        assertNotNull(updatedCard);
        assertEquals("John Doe", updatedCard.getNombreTitular());
    }

    @Test
    public void updateFails() throws SQLException {
        UUID id = UUID.randomUUID();
        TarjetaCredito creditcard = TarjetaCredito.builder()
                .id(id)
                .nombreTitular("Jane Doe")
                .numero("1234567890123456")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(dataBaseManager.connect()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        TarjetaCredito updatedCard = repository.update(id, creditcard);

        assertNull(updatedCard);
    }


    @Test
    public void delete() {
        UUID validId = UUID.randomUUID();

        try {
            when(dataBaseManager.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeUpdate()).thenReturn(1);

            boolean result = repository.delete(validId);

            assertTrue(result);
            verify(statement).setString(1, validId.toString());
            verify(statement).executeUpdate();
        } catch (SQLException e) {
            fail("SQLException should not be thrown");
        }
    }

    @Test
    public void deleteFails() {
        UUID validId = UUID.randomUUID();

        try {
            when(dataBaseManager.connect()).thenThrow(new SQLException("Connection error"));

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(dataBaseManager);
            boolean result = repository.delete(validId);

            assertFalse(result);
        } catch (SQLException e) {
            fail("SQLException should be handled within the method");
        }
    }

    @Test
    public void findAllCreditCardsByUserId_Success() {
        // Arrange
        Long userId = 1L;
        TarjetaCredito tarjetaCredito = TarjetaCredito.builder()
                .id(UUID.randomUUID())
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(userId)
                .fechaCaducidad("12/25")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        List<TarjetaCredito> expectedTarjetas = List.of(tarjetaCredito);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            when(dataBaseManager.connect()).thenReturn(connection);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, false);
            when(resultSet.getObject("uuid")).thenReturn(tarjetaCredito.getId());
            when(resultSet.getString("numero")).thenReturn(tarjetaCredito.getNumero());
            when(resultSet.getString("nombreTitular")).thenReturn(tarjetaCredito.getNombreTitular());
            when(resultSet.getObject("clienteID", String.class)).thenReturn(String.valueOf(tarjetaCredito.getClientID()));
            when(resultSet.getString("fechaCaducidad")).thenReturn(tarjetaCredito.getFechaCaducidad());
            when(resultSet.getObject("createdAt", LocalDateTime.class)).thenReturn(tarjetaCredito.getCreatedAt());
            when(resultSet.getObject("updatedAt", LocalDateTime.class)).thenReturn(tarjetaCredito.getUpdatedAt());
            when(resultSet.getObject("isDeleted", Boolean.class)).thenReturn(tarjetaCredito.getIsDeleted());

            // Act
            List<TarjetaCredito> tarjetas = repository.findAllCreditCardsByUserId(userId);

            // Assert
            assertNotNull(tarjetas);
            assertFalse(tarjetas.isEmpty());
            assertEquals(expectedTarjetas, tarjetas);
        } catch (SQLException e) {
            fail("SQL Exception should not occur");
        }
    }

    @Test
    public void findAllCreditCardsByUserId_NoResults() {
        Long userId = 1L;

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            when(dataBaseManager.connect()).thenReturn(connection);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(statement);
            when(statement.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(false); // No results

            List<TarjetaCredito> tarjetas = repository.findAllCreditCardsByUserId(userId);

            assertNotNull(tarjetas);
            assertTrue(tarjetas.isEmpty());
        } catch (SQLException e) {
            fail("SQL Exception should not occur");
        }


    }

    @Test
    public void findAllCreditCardsByUserId_withException() throws SQLException {
        // Arrange
        Long userId = 1L;
        String query = "SELECT * FROM Tarjeta where clientID = ?";

        // Mockear dataBaseManager.connect para lanzar una SQLException
        when(dataBaseManager.connect()).thenThrow(new SQLException("Simulated exception"));

        // Instanciar el repositorio con sus dependencias
        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(dataBaseManager);

        // Act
        List<TarjetaCredito> result = repository.findAllCreditCardsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty()); // Debería estar vacío porque se maneja la excepción y se devuelve una lista vacía
    }
}