package com.epam.rd.repository;

import com.epam.rd.entity.Catalog;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;

/**
 * Catalog repository for working with catalog table in database
 *
 * @author YurchenkoDD
 */
@Slf4j
public class CatalogRepository implements CrudRepository<Catalog> {

    private final EntityManager entityManager = Persistence.createEntityManagerFactory("store-pu").createEntityManager();

    /**
     * Finds catalog in database by his id
     *
     * @param id of catalog
     * @return found catolog or empty object
     */
    @Override
    public Optional<Catalog> findById(UUID id) {
        try {
            log.info("findById() - find catalog by id = {}", id);
            return ofNullable(entityManager.find(Catalog.class, id));
        } catch (Exception e) {
            log.warn("Error during searching:", e);
        }
        return empty();
    }

    /**
     * Finds list of catalog in database
     *
     * @return list of catalogs or empty list
     */
    @Override
    public List<Catalog> findAll() {
        try {
            log.info("findAll() - find all catalogs");
            TypedQuery<Catalog> query = entityManager.createQuery("from Catalog", Catalog.class);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error during searching", e);
        }
        return Collections.emptyList();
    }

    /**
     * Saves(updates) one catalog record in database
     *
     * @param catalog saving(updating)
     */
    @Override
    public void save(Catalog catalog) {
        try {
            log.info("update() - update catalog {}", catalog);
            entityManager.getTransaction().begin();
            if(catalog.getId() != null){
                entityManager.merge(catalog);
            }else{
                entityManager.persist(catalog);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during saving(updating)", e);
        }

    }

    /**
     * Deletes one catalog record
     *
     * @param catalog for deleting
     */
    @Override
    public void delete(Catalog catalog) {
        try {
            log.info("delete() - delete catalog with id = {}", catalog.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(catalog);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during delete: {} ", catalog.getId());
        }
    }

    /**
     * Check exist catalog by his id
     *
     * @param id of catalog
     * @return false
     */
    @Override
    public boolean existsById(UUID id) {
        try {
            log.info("existById() - check is catalog exist with id = {} ", id);
            return Optional.ofNullable(entityManager.find(Catalog.class, id)).isPresent();
        } catch (Exception e) {
            log.warn("Error during searching by id={} ", id);
        }
        return false;
    }
}
