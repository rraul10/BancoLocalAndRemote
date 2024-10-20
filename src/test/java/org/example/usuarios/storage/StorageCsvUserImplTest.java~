package org.example.usuarios.storage;

import org.example.creditcard.storage.StorageCsvCredCardImpl;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.example.storages.validators.csvValidator;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageCsvUserImplTest {
    @Test
    public void importList() {
        File file = new File(getClass().getClassLoader().getResource("userTest.csv").getFile());

        csvValidator validador = mock(csvValidator.class);
        //when(validador.csvValidatorImport(eq(file))).thenReturn(true);
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);
        Flux<Usuario> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(3) // Assuming the CSV has 3 valid entries
                .verifyComplete();
    }

    @Test
    public void importListWithInvalidFile() {
        File file = new File(getClass().getClassLoader().getResource("userTest.json").getFile());

        csvValidator validador = mock(csvValidator.class);
        //when(validador.csvValidatorImport(eq(file))).thenReturn(false);
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);
        Flux<Usuario> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }
/*
    @Test
    public void importListWithInvalidFormat() {
        File file = new File(getClass().getClassLoader().getResource("userTestInvalidFormat.csv").getFile());
        if (file == null) {
            throw new IllegalArgumentException("Archivo no encontrado");
        }
        System.out.println(file.getAbsolutePath());
        csvValidator validador = mock(csvValidator.class);


        when(csvValidator.csvValidatorImport(any(File.class))).thenReturn(false);
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);
        Flux<Usuario> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }*/

    @Test
    public void importListWithEmptyFile() {
        File file = new File(getClass().getClassLoader().getResource("userTestEmpty.csv").getFile());

        csvValidator validador = mock(csvValidator.class);
        //when(csvValidator.csvValidatorImport(eq(file))).thenReturn(false);
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);
        Flux<Usuario> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    // Successfully exports a list of TarjetaCredito to a CSV file
    @Test
    public void exportList() {

        csvValidator validador = mock(csvValidator.class);

        List<Usuario> usuarios = List.of(
                Usuario.builder().id(1L).name("John").username("john_doe").email("john@example.com").build(),
                Usuario.builder().id(2L).name("Jane").username("jane_doe").email("jane@example.com").build()
        );

        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test_user_output.csv");
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);

        storage.exportList(usuarios, file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            String dataLine = reader.readLine();

            assertEquals("id,nombre,username,email", header);
            assertNotNull(dataLine);
        } catch (IOException e) {
            fail("IOException should not have occurred");
        } finally {
            file.delete();
        }
    }

    @Test
    public void exportListEmpty() {

        csvValidator validador = mock(csvValidator.class);

        List<Usuario> lista = Collections.emptyList();

        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test_user_output.csv");
        StorageCsvUserImpl storage = new StorageCsvUserImpl(validador);

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
}