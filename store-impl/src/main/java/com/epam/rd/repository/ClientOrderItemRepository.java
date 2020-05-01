package com.epam.rd.repository;

import com.epam.rd.entity.ClientOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static java.util.Optional.ofNullable;
import static java.util.Optional.empty;


public class ClientOrderItemRepository implements CrudRepository<ClientOrderItem> {
    private final EntityManager entityManager = Persistence.createEntityManagerFactory("store-pu").createEntityManager();
    private static final Logger log = LoggerFactory.getLogger(ClientOrderItemRepository.class);

    @Override
    public Optional<ClientOrderItem> findById(UUID id) {
        log.info("findById() - find client order item by id = {}", id);
        try {
            return ofNullable(entityManager.find(ClientOrderItem.class, id));
        } catch (Exception e) {
            log.warn("Error during searching by id in ClientOrderItem: ", e);
        }
        return empty();
    }

    @Override
    public List<ClientOrderItem> findAll() {
        log.info("findAll() - find all client order items");
        try{
            return entityManager.createQuery("from ClientOrderItem",ClientOrderItem.class).getResultList();
        }catch (Exception e){
            log.warn("Error during findAll record id in ClientOrderItem: ", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void save(ClientOrderItem object) {
        log.info("save() - save client order item with id = {}", object.getId());
        try {
            entityManager.getTransaction().begin();
            if (object.getId() != null) {
                entityManager.merge(object);
            } else {
                entityManager.persist(object);
            }
            entityManager.flush();
            entityManager.getTransaction().commit();
        }catch(Exception e){
            log.warn("Error during save record id in ClientOrderItem: ", e);
        }
    }

    @Override
    public void delete(ClientOrderItem object) {
        log.info("delete() - delete client order item with id = {}", object.getId());
        try{
            entityManager.getTransaction().begin();
            entityManager.remove(object);
            entityManager.getTransaction().commit();
        }catch (Exception e){
            log.warn("Error during delete record in ClientOrderItem: ", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        log.info("existsById() - check is client order item exist with id = {} ", id);
        return findById(id).isPresent();
    }
}
