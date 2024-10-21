package org.example.creditcard.storage;

import org.example.creditcard.repositories.CreditCardRepository;
import org.example.models.TarjetaCredito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementacion del almacenamiento de tarjetas de credito en formato CSV.
 * Esta clase proporciona metodos para importar y exportar una lista de tarjetas de credito
 * en formato CSV.
 * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
 * @since 1.0
 */

public class StorageCsvCredCardImpl implements StorageCsvCredCard {
    private final Logger logger = LoggerFactory.getLogger(CreditCardRepository.class);

    /**
     * Importa una lista de tarjetas de credito desde un archivo CSV.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param file archivo CSV con la lista de tarjetas de credito
     * @return flujo de tarjetas de credito importadas
     */

    @Override
    public Flux<TarjetaCredito> importList(File file) {
        logger.debug("Import tarjetas de Credito from file: {}", file.getAbsolutePath());
        return Flux.<TarjetaCredito>create(emitter -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.lines()
                        .skip(1)
                        .forEach(line -> {
                            TarjetaCredito tarjetaCredito = parseLine(List.of(line.split(",")));
                            emitter.next(tarjetaCredito);
                        });
                emitter.complete();
            } catch (Exception e) {
                emitter.error(e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Convierte una linea del archivo CSV en una tarjeta de credito.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param linea linea del archivo CSV
     * @return tarjeta de credito
     */

    private TarjetaCredito parseLine(List<String> linea) {
        if (linea.size() < 5) {
            System.out.println(linea);
            throw new IllegalArgumentException("Línea CSV inválida: " + linea);
        }

        return TarjetaCredito.builder()
                .id(UUID.fromString(linea.get(0)))  // Parseamos el id como UUID
                .numero(linea.get(1))
                .nombreTitular(linea.get(2))
                .clientID(Long.parseLong(linea.get(3)))
                .fechaCaducidad(linea.get(4))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }


    /**
     * Exporta una lista de tarjetas de credito a un archivo CSV.
     * @author Raul Fernandez, Alvaro Herrero, Javier Ruiz, Javier Hernandez, Samuel Cortes, Yahya El Hadri.
     * @since 1.0
     * @param lista lista de tarjetas de credito
     * @param file archivo CSV con la lista de tarjetas de credito
     */

    @Override
    public void exportList(List<TarjetaCredito> lista, File file) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,numero,nombreTitular,clientID,fechaCaducidad");
            writer.newLine();

            for (TarjetaCredito tarjeta : lista) {
                String line = tarjeta.getId() + "," +
                        tarjeta.getNumero() + "," +
                        tarjeta.getNombreTitular() + "," +
                        tarjeta.getClientID() + "," +
                        tarjeta.getFechaCaducidad();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
