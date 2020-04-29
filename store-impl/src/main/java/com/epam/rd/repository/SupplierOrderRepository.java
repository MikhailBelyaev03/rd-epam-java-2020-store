package com.epam.rd.repository;

import com.epam.rd.entity.SupplierOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplierOrderRepository implements CrudRepository<SupplierOrder> {
    private static final Logger log = LoggerFactory.getLogger(SupplierOrderRepository.class);
    private final EntityManager entityManager = Persistence
            .createEntityManagerFactory("store-pu")
            .createEntityManager();

    @Override
    public Optional<SupplierOrder> findById(UUID id) {
        log.info("findById() - find supplier order by id= {}", id);
        return Optional.ofNullable(entityManager.find(SupplierOrder.class, id));
    }

    @Override
    public List<SupplierOrder> findAll() {
        log.info("findAll() - find all supplier orders");
        return entityManager.createQuery("from SupplierOrder").getResultList();
    }

    @Override
    public void save(SupplierOrder object) {
        log.info("save() - save supplier order with id= {}", object.getId());
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
    public void delete(SupplierOrder object) {
        log.info("delete() - delete supplier order with id= {}", object.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(object);
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    @Override
    public boolean existsById(UUID id) {
        log.info("existsById() - check supplier order is exists with id= {}", id);
        return findById(id).isPresent();
    }
}
