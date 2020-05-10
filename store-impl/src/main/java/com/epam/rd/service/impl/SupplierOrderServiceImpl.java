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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author Belousov Anton
 * @{code SupplierOrderServiceImpl} realise business-logic of purchasing goods from a supplier
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class SupplierOrderServiceImpl implements SupplierOrderService {

    private static final String STATUS = "IN PROGRESS";

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

        for (UUID productId : keys) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                SupplierOrderItem supplierOrderItem = new SupplierOrderItem();

                //supplierOrder.setId(UUID.randomUUID());

                supplierOrderItem.setProduct(product.get());
                supplierOrderItem.setSupplierOrder(supplierOrder);
                supplierOrderItem.setQuantity(orderItems.get(productId));

                supplierOrder.getSupplierOrderItems().add(supplierOrderItem);

                Catalog catalog = product.get().getCatalog();
                Integer orderItemsSize = orderItems.size();
                Double price = calculatePrice(catalog, orderItemsSize);

                supplierOrder.setPrice(price);
                supplierOrder.setStatus(STATUS);
                supplierOrder.setPaymentCallbackUrl(null);

                UUID paymentID = supplierStubService.send();

                supplierOrder.setPaymentId(paymentID);

                supplierOrderRepository.save(supplierOrder);

                log.info("Purchase completed successfully");
            } else {
                log.info("Purchase finish with error");
            }
        }
        return supplierOrder;
    }

    /**
     * This method calculate price for field price in exemplar {@link SupplierOrder} class
     *
     * @param catalog       - input exemplar {@link Catalog} class for get price by one item
     * @param orderItemSize - input size items in order
     * @return result price for supplier order
     */
    private Double calculatePrice(Catalog catalog, Integer orderItemSize) {
        Double price = catalog.getPrice();
        return price * orderItemSize;
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
            supplierOrderRepository.save(optionalSupplierOrder.get());

            log.info("Changed the status to DELIVERED for SupplierOrder with id = {}", supplierOrderId);
        } else {
            log.info("Order for supplier with id= {} is not exists", supplierOrderId);
        }
    }

    /**
     * This method checks for existence supplier order with concrete payment id
     *
     * @param paymentId - entry ID from SupplierOrder
     * @return if exist supplier order
     *         else null
     */
    @Override
    public SupplierOrder checkOrderByPaymentId(UUID paymentId) {

            Optional<SupplierOrder> supplierOrderOptional = supplierOrderRepository.findByPaymentId(paymentId);
            if (supplierOrderOptional.isPresent()) {
                return supplierOrderOptional.get();
            } else {
                log.warn("Supplier order with payment id= {} is not exists", paymentId);
                return null;
            }
    }
}
