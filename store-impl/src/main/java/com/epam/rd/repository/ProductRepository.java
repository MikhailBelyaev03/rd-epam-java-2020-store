package com.epam.rd.repository;

import com.epam.rd.entity.Product;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;

/**
 * Product repository for working with product table in database
 *
 * @author YurchenkoDD
 */
@Slf4j
public class ProductRepository implements CrudRepository<Product> {

    private final EntityManager entityManager = Persistence.createEntityManagerFactory("store-pu").createEntityManager();

    /**
     * Finds product in database by his id
     *
     * @param id of product
     * @return found product or empty object
     */
    @Override
    public Optional<Product> findById(UUID id) {
        try {
            log.info("findById() - find product by id = {}", id);
            return ofNullable(entityManager.find(Product.class, id));
        } catch (Exception e) {
            log.warn("Error during searching:", e);
        }
        return empty();
    }

    /**
     * Finds list of product in database
     *
     * @return list of product or empty list
     */
    @Override
    public List<Product> findAll() {
        try {
            log.info("findAll() - find all products");
            TypedQuery<Product> query = entityManager.createQuery("from Product", Product.class);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error during searching:", e);
        }
        return Collections.emptyList();
    }

    /**
     * Saves(updates) one product record in database
     *
     * @param product saving(updating)
     */
    @Override
    public void save(Product product) {
        try {
            log.info("update() - update product {}", product);
            entityManager.getTransaction().begin();
            if(product.getId() != null){
                entityManager.merge(product);
            }else{
                entityManager.persist(product);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during saving(updating)", e);
        }

    }

    /**
     * Deletes one product record
     *
     * @param product for deleting
     */
    @Override
    public void delete(Product product) {
        try {
            log.info("delete() - delete product with id = {}", product.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(product);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during delete: {}", product.getId());
        }
    }

    /**
     * Check exist product by his id
     *
     * @param id of product
     * @return false
     */
    @Override
    public boolean existsById(UUID id) {
        try {
            log.info("existById() - check is product exist with id = {} ", id);
            return Optional.ofNullable(entityManager.find(Product.class, id)).isPresent();
        } catch (Exception e) {
            log.warn("Error during searching by id={}", id);
        }
        return false;
    }

}
