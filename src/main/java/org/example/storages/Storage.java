package org.example.storages;

import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

public interface Storage<K> {

    /**
     * Import data from a file
     *
     * @param file the file to import
     * @return a flux of the imported data
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    Flux<K> importList(File file);

    /**
     * Export data to a file
     *
     * @param lista the list of data to export
     * @param file  the file to export to
     * @author Javier Hern ndez, Yahya el hadri, Javier Ruiz, Alvaro herrero, Samuel Cortes, Raul Fernandez
     * @version 1.0
     */
    void exportList(List<K> lista, File file);
}
