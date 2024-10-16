package org.example.storages.validators;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        csvValidator validator = new csvValidator();
        assertTrue(validator.csvValidatorImport(file));
    }


    // File is a directory, not a CSV file
    @Test
    public void test_file_is_directory() {
        File directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(true);

        csvValidator validator = new csvValidator();
        assertFalse(validator.csvValidatorImport(directory));
    }

    @Test
    public void test_file_is_not_csv(){
        File nonCsvFile = mock(File.class);
        when(nonCsvFile.toString()).thenReturn("file.txt");
        assertFalse(csvValidator.csvValidatorImport(nonCsvFile));

    }

    @Test
    public void test_file_cannot_read(){
        File unreadableFile = mock(File.class);
        when(unreadableFile.canRead()).thenReturn(false);
        assertFalse(csvValidator.csvValidatorImport(unreadableFile));
    }

}