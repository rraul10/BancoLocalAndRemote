package org.example.storages.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.creditcard.repositories.CreditCardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.File;

public class jsonValidator {
    private final Logger logger = LoggerFactory.getLogger(jsonValidator.class);
    public boolean jsonValidator (File file){
        String text = file.toString();


        if(file.isDirectory()){
            System.out.println("Error de directorio");
            logger.error("El archivo es un directorio.", file.getAbsolutePath());
            return false;
        }

        if(!text.endsWith(".json")){
            System.out.println("Error de extension");
            logger.error("El archivo no es un json.", file.getAbsolutePath());
            return false;
        }

        if(!file.canRead()){
            System.out.println("Error de lectura");
            logger.error("El archivo no permite la lectura.", file.getAbsolutePath());
            return false;
        }

        //Esto valida que el Json tenga una estructura correcta
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(file);
        } catch (Exception e) {
            System.out.println("Error de estructura");
            logger.error("El archivo no tiene la estructura correcta de un json.", file.getAbsolutePath());
            return false;
        }

        return true;

    }
}
