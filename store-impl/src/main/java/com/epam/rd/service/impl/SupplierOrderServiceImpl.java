package com.epam.rd.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Belousov Anton
 * @{code SupplierOrderServiceImpl} realise business-logic of purchasing goods from a supplier
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
@Service
public class SupplierOrderServiceImpl implements SupplierOrderService {

    private static final String SUPPLIER_ORDER_STATUS_IN_PROGRESS = "IN PROGRESS";
    private static final String SUPPLIER_ORDER_STATUS_DELIVERED = "DELIVERED";
    private static final String SUPPLIER_ORDER_PAYMENT_CALLBACK_URL = null;

    @Autowired
    private SupplierOrderRepository supplierOrderRepository;
    @Autowired
    private SupplierOrderItemRepository supplierOrderItemRepository;
    @Autowired
    private ProductRepository productRepository;

    private SupplierStubService supplierStubService = new SupplierStubService();

    /**
     * This method create a new order
     *
     * @param orderItems - some input items in order
     * @return supplier order
     */
    @Override
    public SupplierOrder create(Map<UUID, Integer> orderItems) {

        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setStatus(SUPPLIER_ORDER_STATUS_IN_PROGRESS);
        supplierOrder.setPaymentCallbackUrl(SUPPLIER_ORDER_PAYMENT_CALLBACK_URL);

        List<SupplierOrderItem> supplierOrderItemList = orderItems.keySet()
                .stream()
                .map(this::findByProductId)
                .map(product -> createSupplierOrderItem(product, orderItems.get(product.getId())))
                .map(supplierOrderItem -> addSupplierOrderInSupplierOrderItem(supplierOrderItem, supplierOrder))
                .collect(Collectors.toList());

        if (supplierOrderItemList.isEmpty()) {
            log.warn("Supplier order has not items");
            throw new RuntimeException("Supplier order items is empty");
        }

        supplierOrder.setSupplierOrderItems(supplierOrderItemList);
        supplierOrder.setPrice(calculatePrice(supplierOrderItemList));
        supplierOrder.setPaymentId(supplierStubService.send());

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
        SupplierOrder supplierOrder = supplierOrderRepository.findById(supplierOrderId).orElseThrow(() -> {
            log.warn("Order for supplier with id = {} is not exists", supplierOrderId);
            return new RuntimeException("Order for supplier not found by supplier order id");
        });
        supplierOrder.setStatus(SUPPLIER_ORDER_STATUS_DELIVERED);
        supplierOrderRepository.save(supplierOrder);

        log.info("Changed the status to PAID for SupplierOrder with id = {}", supplierOrderId);
    }

    /**
     * This method checks for existence supplier order with concrete payment id
     *
     * @param paymentId - entry ID from SupplierOrder
     * @return if exist supplier order
     * @throws RuntimeException if supplier order with concrete payment id is not exists
     */
    @Override
    public SupplierOrder checkOrderByPaymentId(UUID paymentId) {

        return supplierOrderRepository
                .findByPaymentId(paymentId).orElseThrow(() -> {
                    log.warn("Supplier order with payment id = {} is not exists", paymentId);
                    return new RuntimeException("Supplier order not found by payment id");
                });
    }

    /**
     * This method calculate price for field price in exemplar {@link SupplierOrder} class
     *
     * @return result price for supplier order
     */
    private Double calculatePrice(List<SupplierOrderItem> supplierOrderItemList) {
        return supplierOrderItemList.stream()
                .mapToDouble(price -> price.getProduct().getCatalog().getPrice() * price.getQuantity())
                .sum();
    }

    /**
     * This method find product by id
     *
     * @param productId - input product id for find product
     * @return product if exists or {@link RuntimeException} if product is not exists
     */
    private Product findByProductId(UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> {
            log.warn("Product with id = {} is not exists", productId);
            return new RuntimeException("Product not found by product id");
        });
    }

    /**
     * This method create object of {@link SupplierOrderItem} and initialize this object
     *
     * @param product  - input exemplar {@link Product}
     * @param quantity - input quantity from orderItems map
     * @return supplierOrderItem
     */
    private SupplierOrderItem createSupplierOrderItem(Product product, int quantity) {
        SupplierOrderItem supplierOrderItem = new SupplierOrderItem();
        supplierOrderItem.setProduct(product);
        supplierOrderItem.setQuantity(quantity);
        return supplierOrderItem;
    }

    /**
     * This method add field supplierOrder on object of {@link SupplierOrderItem}
     *
     * @param supplierOrderItem - input exemplar {@link SupplierOrderItem}
     * @param supplierOrder     - input exemplar {@link SupplierOrder} for fill in
     * @return fully filled supplierOrderItem
     */
    private SupplierOrderItem addSupplierOrderInSupplierOrderItem(SupplierOrderItem supplierOrderItem, SupplierOrder supplierOrder) {
        supplierOrderItem.setSupplierOrder(supplierOrder);
        return supplierOrderItem;
    }
}
