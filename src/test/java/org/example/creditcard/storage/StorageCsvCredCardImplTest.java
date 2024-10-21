package org.example.creditcard.storage;

import org.example.models.TarjetaCredito;
import org.example.storages.validators.CsvValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class StorageCsvCredCardImplTest {

    @Test
    public void importList() {
        File file = new File(getClass().getClassLoader().getResource("creditCardTest.csv").getFile());
        System.out.println(file.getAbsolutePath());
        CsvValidator validadorCsvCC = mock(CsvValidator.class);
        //when(validadorCsvCC.csvValidatorImport(any(File.class))).thenReturn(true);
        StorageCsvCredCardImpl storage = new StorageCsvCredCardImpl();

        Flux<TarjetaCredito> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(3) // Assuming the CSV has 3 valid entries
                .verifyComplete();
    }

    @Test
    public void importListWithInvalidFile() {
        CsvValidator validador = mock(CsvValidator.class);
        //when(validador.csvValidatorImport(any(File.class))).thenReturn(false);
        File file = new File(getClass().getClassLoader().getResource("creditCardTest.json").getFile());
        StorageCsvCredCardImpl storage = new StorageCsvCredCardImpl();
        Flux<TarjetaCredito> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }

    @Test
    public void importListWithInvalidFormat() {
        CsvValidator validador = mock(CsvValidator.class);
        //when(validador.csvValidatorImport(any(File.class))).thenReturn(false);
        File file = new File(getClass().getClassLoader().getResource("creditCardTestInvalidFormat.csv").getFile());
        StorageCsvCredCardImpl storage = new StorageCsvCredCardImpl();
        Flux<TarjetaCredito> result = storage.importList(file);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyError();
    }

    // Handles non-CSV files by returning an error Flux
    @Test
    public void test_import_non_csv_file() {
        CsvValidator validator = Mockito.mock(CsvValidator.class);
        //Mockito.when(validator.csvValidatorImport(Mockito.any(File.class))).thenReturn(false);

        StorageCsvCredCardImpl storage = new StorageCsvCredCardImpl();
        File nonCsvFile = new File(getClass().getClassLoader().getResource("creditCardTest.json").getFile());

        Flux<TarjetaCredito> result = storage.importList(nonCsvFile);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }


    // Successfully exports a list of TarjetaCredito to a CSV file
        @Test
        public void exportList() {

            CsvValidator validador = mock(CsvValidator.class);
            //when(validador.csvValidatorExport(any(List.class))).thenReturn(true);

            List<TarjetaCredito> lista = List.of(
                    TarjetaCredito.builder()
                            .id(UUID.randomUUID())
                            .numero("1234567890123456")
                            .nombreTitular("John Doe")
                            .clientID(1L)
                            .fechaCaducidad("12/25")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .isDeleted(false)
                            .build()
            );

            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "test_output.csv");
            StorageCsvCredCardImpl storage = new StorageCsvCredCardImpl();

            storage.exportList(lista, file);

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String header = reader.readLine();
                String dataLine = reader.readLine();

                assertEquals("id,numero,nombreTitular,clientID,fechaCaducidad", header);
                assertNotNull(dataLine);
            } catch (IOException e) {
                fail("IOException should not have occurred");
            } finally {
                file.delete();
            }
        }
}