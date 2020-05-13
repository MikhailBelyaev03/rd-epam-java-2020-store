package com.epam.rd.service.impl;

import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.entity.SupplierOrderItem;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderItemRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import com.epam.rd.service.SupplierOrderService;
import com.epam.rd.service.stub.SupplierStubService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Belousov Anton
 * @{code SupplierOrderServiceImpl} realise business-logic of purchasing goods from a supplier
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class SupplierOrderServiceImpl implements SupplierOrderService {

    private static final String SUPPLIER_ORDER_STATUS_IN_PROGRESS = "IN PROGRESS";
    private static final String SUPPLIER_ORDER_STATUS_DELIVERED = "DELIVERED";
    private static final String SUPPLIER_ORDER_PAYMENT_CALLBACK_URL = null;

    private SupplierOrderRepository supplierOrderRepository = new SupplierOrderRepository();
    private SupplierOrderItemRepository supplierOrderItemRepository = new SupplierOrderItemRepository();
    private ProductRepository productRepository = new ProductRepository();

    private SupplierStubService supplierStubService = new SupplierStubService();

    /**
     * This method create a new order
     *
     * @param orderItems - some input items in order
     * @return supplier order
     */
    @Override
    public SupplierOrder create(Map<UUID, Integer> orderItems) {

        Set<UUID> keys = orderItems.keySet();
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setStatus(SUPPLIER_ORDER_STATUS_IN_PROGRESS);
        supplierOrder.setPaymentCallbackUrl(SUPPLIER_ORDER_PAYMENT_CALLBACK_URL);
        SupplierOrderItem supplierOrderItem = new SupplierOrderItem();
        List<SupplierOrderItem> supplierOrderItemList = new ArrayList<>();
        Optional<Product> product = Optional.of(new Product());

        for (UUID productId : keys) {
            try {
                product = productRepository.findById(productId);
                if (product.isPresent()) {
                    supplierOrderItem.setProduct(product.get());
                    supplierOrderItem.setSupplierOrder(supplierOrder);
                    supplierOrderItem.setQuantity(orderItems.get(productId));

                    supplierOrder.getSupplierOrderItems().add(supplierOrderItem);
                    supplierOrderItemList = supplierOrder.getSupplierOrderItems();
                }
            } catch (RuntimeException e) {
                log.warn("Product with id = {} not exists. Error: {}", productId, e.getMessage());
            }
        }

        try {
            if (supplierOrder.getSupplierOrderItems().isEmpty()) {
                log.info("Supplier order has not items");
            }
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
        }

        supplierOrder.setSupplierOrderItems(supplierOrderItemList);

        if (product.isPresent()) {
            Catalog catalog = product.get().getCatalog();
            Long supplierOrderItemQuantity = supplierOrderItem.getQuantity();

            Double price = calculatePrice(catalog, supplierOrderItemQuantity);
            supplierOrder.setPrice(price);
        }

        UUID paymentId = supplierStubService.send();
        supplierOrder.setPaymentId(paymentId);

        supplierOrderRepository.save(supplierOrder);

        log.info("Purchase completed successfully");
        return supplierOrder;
    }

    /**
     * This method change status from "in process" to "delivered"
     *
     * @param supplierOrderId - some input supplier order ID
     * @throws RuntimeException - if order is not exists
     */
    @Override
    public void markAsDelivered(UUID supplierOrderId) {
        Optional<SupplierOrder> optionalSupplierOrder = supplierOrderRepository.findById(supplierOrderId);

        try {
            optionalSupplierOrder.orElseThrow(RuntimeException::new);
            SupplierOrder supplierOrder = optionalSupplierOrder.get();
            supplierOrder.setStatus(SUPPLIER_ORDER_STATUS_DELIVERED);
            supplierOrderRepository.save(supplierOrder);

            log.info("Changed the status to PAID for SupplierOrder with id = {}", supplierOrderId);
        } catch (RuntimeException e) {
            log.warn("Order for supplier with id = {} is not exists", supplierOrderId);
        }
    }

    /**
     * This method checks for existence supplier order with concrete payment id
     *
     * @param paymentId - entry ID from SupplierOrder
     * @return if exist supplier order
     * else null
     */
    @Override
    public SupplierOrder checkOrderByPaymentId(UUID paymentId) {

        try {
            Optional<SupplierOrder> supplierOrderOptional = (Optional<SupplierOrder>) supplierOrderRepository
                    .findByPaymentId(paymentId);

            if (supplierOrderOptional.isPresent()) {
                return supplierOrderOptional.get();
            } else {
                log.warn("Supplier order with payment id= {} is not exists", paymentId);
                return null;
            }
        } catch (ClassCastException e) {
            e.getMessage();
        }
        return null;
    }

    /**
     * This method calculate price for field price in exemplar {@link SupplierOrder} class
     *
     * @param catalog                   - input exemplar {@link Catalog} class for get price by one item
     * @param supplierOrderItemQuantity - input quantity items in order
     * @return result price for supplier order
     */
    private Double calculatePrice(Catalog catalog, Long supplierOrderItemQuantity) {
        Double price = catalog.getPrice();
        return price * supplierOrderItemQuantity;
    }
}
