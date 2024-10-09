package org.example.usuarios.storage;

import org.example.creditcard.repositories.CreditCardRepository;
import org.example.models.TarjetaCredito;
import org.example.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class StorageCsvUserImpl implements StorageCsvUser {
    private final Logger logger = LoggerFactory.getLogger(StorageCsvUser.class);


    @Override
    public Flux<Usuario> importList(File file) {
        logger.debug("Import users from file: {}", file.getAbsolutePath());
        return Flux.<Usuario>create(emiitter -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.lines()
                        .skip(1) // Skip header
                        .forEach(line -> {
                            Usuario usuario = parseLine(List.of(line.split(",")));
                            emiitter.next(usuario);
                        });
                emiitter.complete();
            } catch (Exception e) {
                emiitter.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Usuario parseLine(List<String> linea) {
        return Usuario.builder()
                .id(Long.valueOf(linea.get(0)))
                .name( linea.get(1))
                .username(linea.get(2))
                .email(linea.get(4))
                .build();
    }

    @Override
    public void exportList(List<Usuario> lista, File file) {
        logger.debug("exports users to file: {}", file.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,nombre,username,email");
            writer.newLine();

            for (Usuario usuario : lista) {
                String line = usuario.getId() + "," +
                        usuario.getName() + "," +
                        usuario.getUsername() + "," +
                        usuario.getEmail();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
