package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrderItem;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Belousov Anton
 * @{code SupplierOrderItemRepository} describe work with table st_supplier_order_item on DB.
 */
@Slf4j
public class SupplierOrderItemRepository implements CrudRepository<SupplierOrderItem> {
    private final EntityManager entityManager = Persistence
            .createEntityManagerFactory("store-pu")
            .createEntityManager();

    /**
     * This method find record by ID.
     *
     * @param id - input ID for searching concrete record of SupplierOrderItem
     * @return optional of SupplierOrderItem
     */
    @Override
    public Optional<SupplierOrderItem> findById(UUID id) {
        try {
            log.info("findById() - find supplier order items with id= {}", id);
            return Optional.ofNullable(entityManager.find(SupplierOrderItem.class, id));
        } catch (PersistenceException e) {
            log.warn("Error during searching by id= {}: ", id, e);
        }
        return Optional.empty();
    }

    /**
     * This method show all records.
     *
     * @return list objects of SupplierOrderItems
     */
    @Override
    public List<SupplierOrderItem> findAll() {
        try {
            log.info("findAll() - find all supplier order items");
            return entityManager.createQuery("from SupplierOrder", SupplierOrderItem.class).getResultList();
        } catch (PersistenceException e) {
            log.warn("Error during searching all records: ", e);
        }
        return Collections.emptyList();
    }

    /**
     * This method save new record or update exists record.
     *
     * @param object - input object of SupplierOrderItem for save or update concrete record
     */
    @Override
    public void save(SupplierOrderItem object) {
        try {
            log.info("save() - save supplier order items with id= {}", object.getId());
            entityManager.getTransaction().begin();
            if (entityManager.contains(object)) {
                entityManager.merge(object);
            } else {
                entityManager.persist(object);
            }
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            log.warn("Error during saving record ", e);
        }
    }

    /**
     * This method delete concrete record if exists.
     *
     * @param object - input object of SupplierOrderItems for delete
     */
    @Override
    public void delete(SupplierOrderItem object) {
        try {
            log.info("delete() - delete supplier order items with id= {}", object.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(object);
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            log.warn("Error during deleting by id= {}", object.getId(), e);
        }
    }

    /**
     * This method check for existence record with concrete ID
     *
     * @param id - input ID check concrete record of SupplierOrderItems
     * @return - {@code true} if exists record with concrete ID otherwise {@code false}
     */
    @Override
    public boolean existsById(UUID id) {
        try {
            log.info("existsById() - check supplier order item is exists with id= {}", id);
            return findById(id).isPresent();
        } catch (PersistenceException e) {
            log.warn("Error during existence check by id= {}", id, e);
        }
        return false;
    }
}
