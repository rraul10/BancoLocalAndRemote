package org.example.storages.validators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CsvValidator {
    private static final Logger logger = LoggerFactory.getLogger(CsvValidator.class);
    public static boolean csvValidatorImport(File file){



        if(file.isDirectory()){
            logger.error("El archivo es un directorio.", file.getAbsolutePath());
            return false;
        }

        if(!file.toString().endsWith(".csv")){
            logger.error("El archivo no es un csv.", file.getAbsolutePath());
            return false;
        }

        if(!file.canRead()){
            logger.error("El archivo no permite la lectura.", file.getAbsolutePath());
            return false;
        }

        return true;

    }

}

