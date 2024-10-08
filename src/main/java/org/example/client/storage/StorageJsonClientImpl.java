package org.example.client.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.creditcard.repositories.CreditCardRepository;
import org.example.models.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StorageJsonClientImpl implements StorageJsonClient{
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);

    @Override
    public Flux<Cliente> importList(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        logger.debug("Export import from file: {}", file.getAbsolutePath());
        try {
            return Flux.fromIterable(objectMapper.readValue(file, new TypeReference<List<Cliente>>() {}));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error import clients from file: {}", file.getAbsolutePath());
            return Flux.empty();
        }
    }


    @Override
    public void exportList(List<Cliente> lista, File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        logger.debug("Export clients to file: {}", file.getAbsolutePath());
        try {
            objectMapper.writeValue(file, lista);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error export clients to file: {}", file.getAbsolutePath());
        }
    }
}
