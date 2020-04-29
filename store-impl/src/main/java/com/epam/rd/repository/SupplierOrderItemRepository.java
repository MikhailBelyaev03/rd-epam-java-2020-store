package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplierOrderItemRepository implements CrudRepository<SupplierOrderItem> {
    private static final Logger log = LoggerFactory.getLogger(SupplierOrderItemRepository.class);
    private final EntityManager entityManager = Persistence
            .createEntityManagerFactory("store-pu")
            .createEntityManager();


    @Override
    public Optional findById(UUID id) {
        log.info("findById() - find supplier order items with id= {}", id);
        return Optional.ofNullable(entityManager.find(SupplierOrderItem.class, id));
    }

    @Override
    public List<SupplierOrderItem> findAll() {
        log.info("findAll() - find all supplier order items");
        return entityManager.createQuery("from SupplierOrder").getResultList();
    }

    @Override
    public void save(SupplierOrderItem object) {
        log.info("save() - save supplier order items with id= {}", object.getId());
        entityManager.getTransaction().begin();
        if (object.getId() != null) {
            entityManager.merge(object);
        } else {
            entityManager.persist(object);
        }
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(SupplierOrderItem object) {
        log.info("delete() - delete supplier order items with id= {}", object.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(object);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    @Override
    public boolean existsById(UUID id) {
        log.info("existsById() - check supplier order item is exists with id= {}", id);
        return findById(id).isPresent();
    }
}
