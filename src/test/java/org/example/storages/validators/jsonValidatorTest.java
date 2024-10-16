package org.example.storages.validators;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class jsonValidatorTest {

    // Valid JSON file returns true
    @Test
    public void test_valid_json_file_returns_true() {

        File file = new File(getClass().getClassLoader().getResource("clientTest.json").getFile());
        jsonValidator validator = new jsonValidator();
        boolean result = validator.jsonValidator(file);
        assertTrue(result);
    }

    // File is a directory, returns false
    @Test
    public void test_file_is_directory_returns_false() {
        File directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(true);
        jsonValidator validator = new jsonValidator();
        boolean result = validator.jsonValidator(directory);
        assertFalse(result);
    }

    // File does not have .json extension, returns false
    @Test
    public void test_file_without_json_extension_returns_false() {
        // Prepare
        File file = new File("test.txt");

        // Execute
        boolean result = new jsonValidator().jsonValidator(file);

        // Verify
        assertFalse(result);
    }

    // File cannot be read, returns false
    @Test
    public void test_file_cannot_be_read_returns_false() {
        // Prepare
        File file = new File("non_existent_file.json");

        // Execute
        boolean result = new jsonValidator().jsonValidator(file);

        // Verify
        assertFalse(result);
    }

    // Invalid JSON file returns false
    @Test
    public void test_invalid_json_file_returns_false() {
        jsonValidator validator = new jsonValidator();
        File invalidJsonFile = new File("invalid.json");
        boolean result = validator.jsonValidator(invalidJsonFile);
        assertFalse(result);
    }

    // Empty JSON file returns false
    @Test
    public void test_empty_json_file_returns_false() {
        jsonValidator validator = new jsonValidator();
        File emptyJsonFile = new File("empty.json");
        boolean result = validator.jsonValidator(emptyJsonFile);
        assertFalse(result);
    }




}