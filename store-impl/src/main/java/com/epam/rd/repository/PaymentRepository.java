package com.epam.rd.repository;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import com.epam.rd.entity.Payment;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Payment repository for working with payment table in database
 *
 * @author YurchenkoDD
 */
@Slf4j
public class PaymentRepository implements CrudRepository<Payment> {

    private static final String FIND_BY_SUPPLIER_ORDER_ID = "FROM Payment where supplier_order_id = :supplierOrderId";
    private final EntityManager entityManager = Persistence.createEntityManagerFactory("store-pu").createEntityManager();

    /**
     * Finds payment in database by his id
     *
     * @param id of payment
     * @return found payment or empty object
     */
    @Override
    public Optional<Payment> findById(UUID id) {
        try {
            log.info("findById() - find payment by id = {}", id);
            return ofNullable(entityManager.find(Payment.class, id));
        } catch (Exception e) {
            log.warn("Error during searching:", e);
        }
        return empty();
    }

    /**
     * Finds list of payment in database
     *
     * @return list of payments or empty list
     */
    @Override
    public List<Payment> findAll() {
        try {
            log.info("findAll() - find all payments = {}");
            TypedQuery<Payment> query = entityManager.createQuery("FROM Payment", Payment.class);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error during searching:", e);
        }
        return Collections.emptyList();
    }

    /**
     * Saves(updates) one payment record in database
     *
     * @param payment saving(updating)
     */
    @Override
    public void save(Payment payment) {
        try {
            log.info("update() - update payment {}", payment);
            entityManager.getTransaction().begin();
            if (payment.getId() != null) {
                entityManager.merge(payment);
            } else {
                entityManager.persist(payment);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during saving(updating)", e);
        }
    }

    /**
     * Deletes one payment record
     *
     * @param payment for deleting
     */
    @Override
    public void delete(Payment payment) {
        try {
            log.info("delete() - delete payment with id = {}", payment.getId());
            entityManager.getTransaction().begin();
            entityManager.remove(payment);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            log.warn("Error during delete: {}", payment.getId());
        }
    }

    /**
     * Check exist payment by his id
     *
     * @param id of payment
     * @return false
     */
    @Override
    public boolean existsById(UUID id) {
        try {
            log.info("existById() - check is payment exist with id = {} ", id);
            return Optional.ofNullable(entityManager.find(Payment.class, id)).isPresent();
        } catch (Exception e) {
            log.warn("Error during searching by id={} ", id);
        }
        return false;
    }

    public Optional<Payment> findBySupplierOrderId(UUID supplierOrderId) {
        try {
            Query query = entityManager.createQuery(FIND_BY_SUPPLIER_ORDER_ID);
            query.setParameter("supplierOrderId", supplierOrderId);
            Optional<Payment> paymentOptional = ofNullable((Payment) query.getSingleResult());
            log.info("findByPaymentID - find supplier order with payment id= {}", supplierOrderId);
            return paymentOptional;
        } catch (Exception e) {
            log.warn("Error during searching by payment id = {}, Message: {}", supplierOrderId, e.getMessage());
        }
        return Optional.empty();
    }
}


