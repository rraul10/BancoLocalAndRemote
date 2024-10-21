package org.example.users.storage;

import org.example.creditcard.repositories.CreditCardRepository;
import org.example.models.Usuario;
import org.example.storages.validators.CsvValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.util.List;

public class StorageCsvUserImpl implements StorageCsvUser {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);
    private final CsvValidator validador;

    public StorageCsvUserImpl(CsvValidator validador) {
        this.validador = validador;
    }

    /**
     * Lee un archivo CSV y devuelve un flujo de usuarios.
     * @param file Archivo CSV con los datos de los usuarios.
     * @return Flujo de usuarios leidos del archivo.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    @Override
    public Flux<Usuario> importList(File file) {
        logger.debug("Import users from file: {}", file.getAbsolutePath());
        if(!validador.csvValidatorImport(file)){
            return Flux.error(new RuntimeException("Error import users from file: " + file.getAbsolutePath()));
        };
        return Flux.<Usuario>create(emitter -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.lines()
                        .skip(1) // Skip header
                        .forEach(line -> {
                            Usuario usuario = parseLine(List.of(line.split(",")));
                            emitter.next(usuario);
                        });
                emitter.complete();
            } catch (Exception e) {
                emitter.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Convierte una l nea leida de un archivo CSV en un objeto {@link Usuario}.
     * @param linea lista de String con los valores de una l nea del archivo CSV.
     * @return objeto {@link Usuario} con los valores de la l nea.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
    private Usuario parseLine(List<String> linea) {
        return Usuario.builder()
                .id(Long.valueOf(linea.get(0)))
                .name( linea.get(1))
                .username(linea.get(2))
                .email(linea.get(4))
                .build();
    }

    /**
     * Guarda una lista de usuarios en un archivo CSV.
     * @param lista lista de usuarios a guardar.
     * @param file Archivo CSV donde se guardar n los usuarios.
     * @author Alvaro Herrero, Javier Ruiz, Javier Hernandez, Raul Fernandez, Yahya El Hadri, Samuel Cortes.
     * @version 1.0
     */
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
