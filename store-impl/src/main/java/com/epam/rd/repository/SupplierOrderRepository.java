package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrder;
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
 * @{code SupplierOrderRepository} describe work with table st_supplier_order on DB.
 */
@Slf4j
public class SupplierOrderRepository implements CrudRepository<SupplierOrder> {
    private final EntityManager entityManager = Persistence
            .createEntityManagerFactory("store-pu")
            .createEntityManager();

    /**
     * This method find record by ID.
     *
     * @param id - input ID for searching concrete record of SupplierOrder
     * @return optional of SupplierOrder
     */
    @Override
    public Optional<SupplierOrder> findById(UUID id) {
        try {
            log.info("findById() - find supplier order by id= {}", id);
            return Optional.ofNullable(entityManager.find(SupplierOrder.class, id));
        } catch (PersistenceException e) {
            log.warn("Error during searching by id= {}", id, e);
        }
        return Optional.empty();
    }

    /**
     * This method show all records.
     *
     * @return list objects of SupplierOrder
     */
    @Override
    public List<SupplierOrder> findAll() {
        try {
            log.info("findAll() - find all supplier orders");
            return entityManager.createQuery("from SupplierOrder", SupplierOrder.class).getResultList();
        } catch (PersistenceException e) {
            log.warn("Error during searching all records: ", e);
        }
        return Collections.emptyList();
    }

    /**
     * This method save new record or update exists record.
     *
     * @param object - input object of SupplierOrder for save or update concrete record
     */
    @Override
    public void save(SupplierOrder object) {
        try {
            log.info("save() - save supplier order with id= {}", object.getId());
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
     * @param object - input object of SupplierOrder for delete
     */
    @Override
    public void delete(SupplierOrder object) {
        try {
            log.info("delete() - delete supplier order with id= {}", object.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(object);
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (PersistenceException e) {
            log.warn("Error during deleting by id= {}", object.getId(), e);
        }
    }

    /**
     * This method checks for existence record with concrete ID
     *
     * @param id - input ID check concrete record of SupplierOrder
     * @return - {@code true} if exist record with concrete ID otherwise {@code false}
     */
    @Override
    public boolean existsById(UUID id) {
        try {
            log.info("existsById() - check supplier order is exists with id= {}", id);
            return findById(id).isPresent();
        } catch (PersistenceException e) {
            log.warn("Error during existence check by id= {}", id, e);
        }
        return false;
    }

    /**
     * This method find record by payment ID
     *
     * @param paymentId - entry payment ID
     * @return optional of SupplierOrder
     */
    public Optional<SupplierOrder> findByPaymentId(UUID paymentId) {
        try {
            log.info("findByPaymentID - find supplier order with payment id= {}", paymentId);
            Optional<SupplierOrder> supplierOrdersOptional = findAll()
                    .stream()
                    .filter(supplierOrder -> supplierOrder.getPaymentId().equals(paymentId))
                    .findFirst();
            return supplierOrdersOptional;
        } catch (PersistenceException e) {
            log.warn("Error during searching by payment id = {}", paymentId);
        }
        return Optional.empty();
    }
}
