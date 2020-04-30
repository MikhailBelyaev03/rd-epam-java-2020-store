package com.epam.rd.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @param <T> - type of value
 * @author Belousov Anton
 * @{code CrudRepository} template methods for some concrete realisation on implemented classes
 */
public interface CrudRepository<T> {

    Optional<T> findById(UUID id);

    List<T> findAll();

    void save(T object);

    void delete(T object);

    boolean existsById(UUID id);
}