package com.epam.rd.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrudRepository<T> {

    Optional<T> findById(UUID id);

    List<T> findAll();

    void save(T object);

    void delete(T object);

    boolean existsById(UUID id);
}