package org.example.storages;

import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

public interface Storage<K> {

    Flux<K> importList(File file);
    void exportList(List<K> lista, File file);

}
