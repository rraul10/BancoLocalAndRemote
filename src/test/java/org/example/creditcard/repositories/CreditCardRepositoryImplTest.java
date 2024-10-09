package org.example.creditcard.repositories;

import org.example.creditcard.database.DataBaseManager;
import java.sql.*;
import org.example.models.TarjetaCredito;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @BeforeAll
    static void setAllUp(){

    }
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void getAll() {
        DataBaseManager mockDataBaseManager = mock(DataBaseManager.class);
        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);

        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {

            Connection mockConnection = mock(Connection.class);
            PreparedStatement mockStatement = mock(PreparedStatement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            when(mockDataBaseManager.connect()).thenReturn(mockConnection);
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false);
            when(mockResultSet.getObject("uuid")).thenReturn(UUID.randomUUID());
            when(mockResultSet.getString("numero")).thenReturn("1234567890123456");
            when(mockResultSet.getString("nombreTitular")).thenReturn("John Doe");
            when(mockResultSet.getObject("clientID")).thenReturn(UUID.randomUUID());
            when(mockResultSet.getString("fechaCaducidad")).thenReturn("12/99");
            when(mockResultSet.getObject("created_at", LocalDateTime.class)).thenReturn(LocalDateTime.now());
            when(mockResultSet.getObject("updated_at", LocalDateTime.class)).thenReturn(LocalDateTime.now());
            when(mockResultSet.getObject("isDeleted", boolean.class)).thenReturn(false);

            System.out.println();

            List<TarjetaCredito> tarjetas = repository.getAll();

            Assertions.assertNotNull(tarjetas);
            Assertions.assertFalse(tarjetas.isEmpty());
        } catch (SQLException e) {
            Assertions.fail("SQL Exception should not occur");
        }

    }


    @Test
    public void getAllFails() {
        DataBaseManager mockDataBaseManager = mock(DataBaseManager.class);

        try {
            when(mockDataBaseManager.connect()).thenThrow(new SQLException("Connection failed"));
        } catch (SQLException e) {
            fail("Mock setup failed");
        }

        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);
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
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        DataBaseManager mockDataBaseManager = mock(DataBaseManager.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        try {
            when(mockDataBaseManager.connect()).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            doReturn(expectedCard.getId()).when(mockResultSet).getObject("uuid", UUID.class);
            doReturn(expectedCard.getNumero()).when(mockResultSet).getString("numero");
            doReturn(expectedCard.getNombreTitular()).when(mockResultSet).getString("nombreTitular");
            doReturn(expectedCard.getClientID()).when(mockResultSet).getObject("clientID", UUID.class);
            doReturn(expectedCard.getFechaCaducidad()).when(mockResultSet).getString("fechaCaducidad");
            doReturn(expectedCard.getCreatedAt()).when(mockResultSet).getObject("created_at", LocalDateTime.class);
            doReturn(expectedCard.getUpdatedAt()).when(mockResultSet).getObject("updated_at", LocalDateTime.class);
            doReturn(expectedCard.getIsDeleted()).when(mockResultSet).getObject("isDeleted", Boolean.class);

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);
            Optional<TarjetaCredito> result = repository.getById(validId);

            Assertions.assertTrue(result.isPresent());
            Assertions.assertEquals(expectedCard, result.get());
        } catch (SQLException e) {
            Assertions.fail("SQLException was thrown");
        }
    }


    @Test
    public void getByIdNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        DataBaseManager mockDataBaseManager = Mockito.mock(DataBaseManager.class);
        Connection mockConnection = Mockito.mock(Connection.class);
        PreparedStatement mockStatement = Mockito.mock(PreparedStatement.class);
        ResultSet mockResultSet = Mockito.mock(ResultSet.class);

        try {
            Mockito.when(mockDataBaseManager.connect()).thenReturn(mockConnection);
            Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
            Mockito.when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            Mockito.when(mockResultSet.next()).thenReturn(false);

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);
            Optional<TarjetaCredito> result = repository.getById(nonExistentId);

            Assertions.assertFalse(result.isPresent());
        } catch (SQLException e) {
            Assertions.fail("SQL Exception should not occur");
        }
    }


    @Test
    public void getByIdFails() {
        UUID anyId = UUID.randomUUID();

        DataBaseManager mockDbManager = Mockito.mock(DataBaseManager.class);

        try {
            Mockito.when(mockDbManager.connect()).thenThrow(new SQLException("Connection failed"));

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDbManager);
            Optional<TarjetaCredito> result = repository.getById(anyId);

            Assertions.assertFalse(result.isPresent());
        } catch (SQLException e) {
            Assertions.fail("SQLException should be handled within the method");
        }
    }



    @Test
    public void create() throws SQLException {

        DataBaseManager mockDataBaseManager = mock(DataBaseManager.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        TarjetaCredito creditCard = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(1).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(mockDataBaseManager.connect()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn(UUID.randomUUID().toString());

        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);


        TarjetaCredito result = repository.create(creditCard);


        assertNotNull(result.getId());
        assertEquals(creditCard.getNumero(), result.getNumero());
        assertEquals(creditCard.getNombreTitular(), result.getNombreTitular());
    }


    @Test
    public void createFails() throws SQLException {

        DataBaseManager mockDataBaseManager = mock(DataBaseManager.class);

        TarjetaCredito creditCard = TarjetaCredito.builder()
                .numero("1234567890123456")
                .nombreTitular("John Doe")
                .clientID(2l)
                .fechaCaducidad(LocalDate.now().plusYears(3).toString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        when(mockDataBaseManager.connect()).thenThrow(new SQLException("Connection failed"));

        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDataBaseManager);


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

        DataBaseManager dataBaseManager = mock(DataBaseManager.class);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);

        when(dataBaseManager.connect()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(dataBaseManager);
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

        DataBaseManager dataBaseManager = mock(DataBaseManager.class);
        Connection connection = mock(Connection.class);

        when(dataBaseManager.connect()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(dataBaseManager);
        TarjetaCredito updatedCard = repository.update(id, creditcard);

        assertNull(updatedCard);
    }


    @Test
    public void delete() {
        UUID validId = UUID.randomUUID();
        DataBaseManager mockDbManager = Mockito.mock(DataBaseManager.class);
        Connection mockConnection = Mockito.mock(Connection.class);
        PreparedStatement mockStatement = Mockito.mock(PreparedStatement.class);

        try {
            Mockito.when(mockDbManager.connect()).thenReturn(mockConnection);
            Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockStatement);
            Mockito.when(mockStatement.executeUpdate()).thenReturn(1);

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDbManager);
            boolean result = repository.delete(validId);

            Assertions.assertTrue(result);
            Mockito.verify(mockStatement).setString(1, validId.toString());
            Mockito.verify(mockStatement).executeUpdate();
        } catch (SQLException e) {
            Assertions.fail("SQLException should not be thrown");
        }
    }

    @Test
    public void deleteFails() {
        UUID validId = UUID.randomUUID();
        DataBaseManager mockDbManager = Mockito.mock(DataBaseManager.class);

        try {
            Mockito.when(mockDbManager.connect()).thenThrow(new SQLException("Connection error"));

            CreditCardRepositoryImpl repository = new CreditCardRepositoryImpl(mockDbManager);
            boolean result = repository.delete(validId);

            Assertions.assertFalse(result);
        } catch (SQLException e) {
            Assertions.fail("SQLException should be handled within the method");
        }
    }
}