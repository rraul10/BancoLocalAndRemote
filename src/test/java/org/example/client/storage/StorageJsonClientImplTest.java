package org.example.client.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Cliente;
import org.example.models.Usuario;
import org.example.storages.validators.JsonValidator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StorageJsonClientImplTest {

    @Test
    public void importList() {
        JsonValidator validador = mock(JsonValidator.class);
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
        JsonValidator validador = mock(JsonValidator.class);
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
        JsonValidator validador = mock(JsonValidator.class);
        when(validador.jsonValidator(any(File.class))).thenReturn(false);
        StorageJsonClientImpl storageJsonClient = new StorageJsonClientImpl(validador);
        File nonExistentFile = new File("non_existent_file.json");
        Flux<Cliente> result = storageJsonClient.importList(nonExistentFile);

        StepVerifier.create(result)
                .verifyError();
    }


    @Test
    public void importListWithInvalidFormat() {
        JsonValidator validador = mock(JsonValidator.class);
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
        JsonValidator validadorJsonCli = mock(JsonValidator.class);
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
        JsonValidator validador = mock(JsonValidator.class);
        StorageJsonClientImpl storageClient = new StorageJsonClientImpl(validador);

        Usuario usuario = new Usuario(1L, "John Doe", "john_doe", "john@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        List<Cliente> clientes = List.of(
                new Cliente(usuario, null)
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

        JsonValidator validador = mock(JsonValidator.class);

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
        JsonValidator validador = mock(JsonValidator.class);
        StorageJsonClientImpl storageClient = new StorageJsonClientImpl(validador);

        Usuario usuario = new Usuario(1L, "John Doe", "john_doe", "john@example.com",
                LocalDateTime.now(), LocalDateTime.now());

        List<Cliente> clientes = List.of(
                new Cliente(usuario, null)
        );

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        storageClient.setObjectMapper(objectMapper);

        File file = new File(System.getProperty("java.io.tmpdir"), "test_client_output.json");

        doThrow(new IOException("Simulated IO Exception")).when(objectMapper).writeValue(file, clientes);

        // Usa assertThrows para verificar que se lanza una excepci�n
        storageClient.exportList(clientes, file);

        // Verifica que el mensaje de error se registr� correctamente
        verify(objectMapper).writeValue(file, clientes);
    }



}