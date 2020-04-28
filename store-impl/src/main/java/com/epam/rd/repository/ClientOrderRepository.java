package com.epam.rd.repository;

import com.epam.rd.entity.ClientOrder;
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

public class ClientOrderRepository implements CrudRepository<ClientOrder> {
    private final EntityManager entityManager = Persistence.createEntityManagerFactory("store-pu").createEntityManager();
    private static final Logger log = LoggerFactory.getLogger(ClientOrderRepository.class);

    @Override
    public Optional<ClientOrder> findById(UUID id) {
        log.info("findById() - find client order by id = {}", id);
        try{
            return ofNullable(entityManager.find(ClientOrder.class, id));
        } catch (Exception e) {
            log.warn("Error during searching by id in ClientOrder: ", e);
        }

        return empty();
    }

    @Override
    public List<ClientOrder> findAll() {
        log.info("findAll() - find all client orders");
        try{
            return entityManager.createQuery("from ClientOrder",ClientOrder.class).getResultList();
        }catch (Exception e){
            log.warn("Error during findAll record id in ClientOrder: ", e);
        }
        return  Collections.emptyList();
    }

    @Override
    public void save(ClientOrder object) {
        log.info("save() - save client order with id = {}", object.getId());
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
            log.warn("Error during save record id in ClientOrder: ", e);
        }
    }

    @Override
    public void delete(ClientOrder object) {
        log.info("delete() - delete client order with id = {}", object.getId());
        try{
            entityManager.getTransaction().begin();
            entityManager.remove(object);
            entityManager.getTransaction().commit();
        }
        catch (Exception e){
            log.warn("Error during delete record in ClientOrder: ", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        log.info("existsById() - check is client order exist with id = {} ", id);
        return findById(id).isPresent();
    }
}
