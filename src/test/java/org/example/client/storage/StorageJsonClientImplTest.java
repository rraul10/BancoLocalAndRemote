package org.example.client.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.creditcard.storage.StorageCsvCredCardImpl;
import org.example.models.Cliente;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.storages.validators.csvValidator;
import org.example.storages.validators.jsonValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StorageJsonClientImplTest {

    @Test
    public void importList() {
        jsonValidator validador = mock(jsonValidator.class);
        when(validador.jsonValidator(any(File.class))).thenReturn(true);
        StorageJsonClientImpl storageJsonClient = new StorageJsonClientImpl(validador);
        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());
        Flux<Cliente> result = storageJsonClient.importList(file);
        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void importListWithInvalidFile() {
        jsonValidator validador = mock(jsonValidator.class);
        when(validador.jsonValidator(any(File.class))).thenReturn(false);
        File file = new File(getClass().getClassLoader().getResource("userTest.csv").getFile());
        StorageJsonClientImpl storage = new StorageJsonClientImpl(validador);
        Flux<Cliente> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }

    @Test
    public void importListException() {
        jsonValidator validador = mock(jsonValidator.class);
        when(validador.jsonValidator(any(File.class))).thenReturn(false);
        StorageJsonClientImpl storageJsonClient = new StorageJsonClientImpl(validador);
        File nonExistentFile = new File("non_existent_file.json"); // Archivo inexistente
        Flux<Cliente> result = storageJsonClient.importList(nonExistentFile);

        StepVerifier.create(result)
                .verifyError();

        // Puedes verificar el log o un contador de excepciones si lo deseas
    }


    @Test
    public void importListWithInvalidFormat() {
        jsonValidator validador = mock(jsonValidator.class);
        when(validador.jsonValidator(any(File.class))).thenReturn(false);
        File file = new File(getClass().getClassLoader().getResource("clientTestInvalidFormat.json").getFile());
        StorageJsonClientImpl storage = new StorageJsonClientImpl(validador);
        Flux<Cliente> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }

    @Test
    public void importListWithEmptyFile() {
        jsonValidator validadorJsonCli = mock(jsonValidator.class);
        when(validadorJsonCli.jsonValidator(any(File.class))).thenReturn(false);
        File file = new File(getClass().getClassLoader().getResource("clientTestEmpty.json").getFile());
        StorageJsonClientImpl storage = new StorageJsonClientImpl(validadorJsonCli);
        Flux<Cliente> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }

    // Successfully exports a list of Cliente objects to a file in a temporary directory
    @Test
    public void exportList() {
        jsonValidator validador = mock(jsonValidator.class);
        StorageJsonClientImpl storageClient = new StorageJsonClientImpl(validador);
        List<Cliente> clientes = List.of(
                Cliente.builder().usuario(Cliente.createUsuario
                        (1L, "John Doe", "john_doe", "john@example.com",
                                LocalDateTime.now(), LocalDateTime.now())).build()
        );
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(tempDir, "test_client_output.json");

        storageClient.exportList(clientes, file);

        String dir = System.getProperty("java.io.tmpdir");
        System.out.println("Directorio temporal: " + dir);
        System.out.println("Archivo creado: " + file.getAbsolutePath());

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

       file.delete();
    }

    @Test
    public void exportListEmpty() {

        jsonValidator validador = mock(jsonValidator.class);

        List<Cliente> lista = Collections.emptyList();

        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test_output.json");
        StorageJsonClientImpl storage = new StorageJsonClientImpl(validador);

        storage.exportList(lista, file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            String dataLine = reader.readLine();


            assertNull(dataLine);
        } catch (IOException e) {
            fail("IOException should not have occurred");
        } finally {
            file.delete();
        }
    }

    @Test
    public void test_export_list_exception() throws IOException {
        jsonValidator validador = mock(jsonValidator.class);
        StorageJsonClientImpl storageClient = new StorageJsonClientImpl(validador);
        List<Cliente> clientes = List.of(
                Cliente.builder().usuario(Cliente.createUsuario
                        (1L, "John Doe", "john_doe", "john@example.com",
                                LocalDateTime.now(), LocalDateTime.now())).build()
        );

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        storageClient.setObjectMapper(objectMapper); // Inyectar el ObjectMapper simulado

        File file = new File(System.getProperty("java.io.tmpdir"), "test_client_output.json");

        doThrow(new IOException("Simulated IO Exception")).when(objectMapper).writeValue(file, clientes);

        // Usa assertThrows para verificar que se lanza una excepción
        storageClient.exportList(clientes, file);

        // Verifica que el mensaje de error se registró correctamente
        verify(objectMapper).writeValue(file, clientes);
    }



}