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

import java.util.UUID;
import java.util.Map;
import java.util.Set;
import java.util.Optional;

/**
 * @author Belousov Anton
 * @{code SupplierOrderServiceImpl} realise business-logic of purchasing goods from a supplier
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class SupplierOrderServiceImpl implements SupplierOrderService {
    private SupplierOrderRepository supplierOrderRepository = new SupplierOrderRepository();
    private SupplierOrderItemRepository supplierOrderItemRepository = new SupplierOrderItemRepository();
    private ProductRepository productRepository = new ProductRepository();

    /**
     * This method create a new order
     *
     * @param orderItems - some input order items
     * @return supplier order
     */
    @Override
    public SupplierOrder create(Map<UUID, Integer> orderItems) {

        Set<UUID> keys = orderItems.keySet();
        SupplierOrder supplierOrder = new SupplierOrder();

        for (UUID productId : keys) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                SupplierOrderItem supplierOrderItem = new SupplierOrderItem();

                supplierOrderItem.setProduct(product.get());
                supplierOrderItem.setSupplierOrder(supplierOrder);
                supplierOrderItem.setQuantity(orderItems.get(productId));

                supplierOrder.getSupplierOrderItems().add(supplierOrderItem);

                Catalog catalog = new Catalog();
                double price = catalog.getPrice();

                supplierOrder.setPrice(price * orderItems.size());
                supplierOrder.setStatus("IN PROGRESS");
                supplierOrder.setPaymentCallbackUrl(null);

                SupplierStubService supplierStubService = new SupplierStubService();
                UUID paymentID = supplierStubService.send();

                supplierOrder.setPaymentId(paymentID);

                supplierOrderItemRepository.save(supplierOrderItem);
                supplierOrderRepository.save(supplierOrder);

                log.info("Purchase completed successfully");
            } else {
                log.info("Purchase finish with error");
            }
        }
        return supplierOrder;
    }

    /**
     * This method change status from "in process" to delivered
     *
     * @param supplierOrderId - some input supplier order ID
     */
    @Override
    public void markAsDelivered(UUID supplierOrderId) {
        Optional<SupplierOrder> optionalSupplierOrder = supplierOrderRepository.findById(supplierOrderId);
        if (optionalSupplierOrder.isPresent()) {
            optionalSupplierOrder.get().setStatus("DELIVERED");

            log.info("Status changed");
        } else {
            log.info("Order for supplier with id= {} is not exists", supplierOrderId);
        }
    }

    /**
     * This method checks for existence supplier order with concrete payment id
     *
     * @param paymentId - entry ID from SupplierOrder
     * @return supplier order or exception
     */
    @Override
    public SupplierOrder checkOrderByPaymentId(UUID paymentId) {
        try {
            SupplierOrder supplierOrder = new SupplierOrder();
            Optional<SupplierOrder> supplierOrderOptional = supplierOrderRepository.findByPaymentId(paymentId);
            if (supplierOrderOptional.isPresent()) {
                return supplierOrder;
            }
        } catch (NullPointerException e) {
            log.warn("Supplier order with payment id= {} is not exists", paymentId);
        }
        return null;
    }
}
