package org.example.storages.validators;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class csvValidatorTest {

    @Test
    public void test_valid_csv_file() {
        File file = new File(getClass().getClassLoader().getResource("creditCardTest.csv").getFile());

        // Verifica que el archivo existe y es legible
        assertTrue(file.exists());
        assertTrue(file.canRead());

        // Llama al validador con el archivo real
        CsvValidator validator = new CsvValidator();
        assertTrue(validator.csvValidatorImport(file));
    }


    // File is a directory, not a CSV file
    @Test
    public void test_file_is_directory() {
        File directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(true);

        CsvValidator validator = new CsvValidator();
        assertFalse(validator.csvValidatorImport(directory));
    }

    @Test
    public void test_file_is_not_csv(){
        File nonCsvFile = mock(File.class);
        when(nonCsvFile.toString()).thenReturn("file.txt");
        assertFalse(CsvValidator.csvValidatorImport(nonCsvFile));

    }

    @Test
    public void test_file_cannot_read(){
        File unreadableFile = mock(File.class);
        when(unreadableFile.canRead()).thenReturn(false);
        assertFalse(CsvValidator.csvValidatorImport(unreadableFile));
    }

}