package com.epam.rd.service.impl;

import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.entity.SupplierOrderItem;
import com.epam.rd.repository.CatalogRepository;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import com.epam.rd.service.ProductService;
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
 * {@code ProductServiceImpl} realise business-logic work with goods
 *
 * @author Belousov Anton
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private SupplierOrderRepository supplierOrderRepository;
    @Autowired
    private SupplierOrderServiceImpl supplierOrderService;

    /**
     * This method calculate stock of a products and create new order if stock less then 100
     */
    @Override
    public void calculateStock() {
        Map<UUID, Integer> productMap;

        List<Product> productList = (List<Product>) productRepository.findAll();

        productMap = productList.stream()
                .filter(product -> this.extractQuantity(product) < 100)
                .collect(Collectors.toMap(this::getListUuid,
                        this::getNeedQuantity));

        if (productMap.isEmpty()) {
            log.info("All goods in stock");
        }

        supplierOrderService.create(productMap);
    }

    /**
     * This method change status supplier order to "DELIVERED" and change quantity of all products arrived in {@link Catalog}
     *
     * @param deliveredSupplierOrderId - input supplier order id, who delivered this order
     */
    @Override
    public void receiveDelivery(UUID deliveredSupplierOrderId) {
        SupplierOrder supplierOrder = supplierOrderRepository.findById(deliveredSupplierOrderId).orElseThrow(() -> {
            log.warn("Supplier order with id = {} is not exists", deliveredSupplierOrderId);
            return new RuntimeException("Supplier order not found by supplier order id");
        });

        supplierOrderService.markAsDelivered(deliveredSupplierOrderId);

        supplierOrder.getSupplierOrderItems().forEach(supplierOrderItem -> {
            Product product = supplierOrderItem.getProduct();
            product.getCatalog().setQuantity(calculateNewQuantity(product, supplierOrderItem));
            catalogRepository.save(product.getCatalog());
        });
    }

    /**
     * This method reserve some products for order and change free quantity this product for purchase in {@link Catalog}
     *
     * @param productId - input id product for reserve this product
     * @param quantity  - input quantity product for reserve
     */
    @Override
    public void reserveProduct(UUID productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.warn("Product with id = {} is not exists", productId);
            return new RuntimeException("Product not found by product id");
        });

        Catalog catalog = product.getCatalog();

        if (extractQuantity(product) < quantity) {
            log.warn("The quantity of goods in stock is less than necessary for the order");
            throw new RuntimeException("The quantity of goods in stock is less than necessary for the order");
        }

        catalog.setQuantity(extractQuantity(product) - quantity);

        catalogRepository.save(catalog);
    }

    /**
     * This method get quantity of concrete product
     *
     * @param product - input concrete product
     * @return quantity of concrete product
     */
    private Integer extractQuantity(Product product) {
        return product.getCatalog().getQuantity();
    }

    /**
     * This method calculate needed quantity of concrete product for order
     *
     * @param product - input concrete product
     * @return needed quantity product for order
     */
    private Integer getNeedQuantity(Product product) {
        return 100 - product.getCatalog().getQuantity();
    }

    /**
     * This method ger product id by concrete product
     *
     * @param product - input concrete product
     * @return product id
     */
    private UUID getListUuid(Product product) {
        return product.getId();
    }

    /**
     * This method calculate new quantity of all products arrived in {@link Catalog}
     *
     * @param product           - input exemplar {@link Product} for get current quantity in {@link Catalog}
     * @param supplierOrderItem - input exemplar {@link SupplierOrderItem} for get arrived quantity in order
     * @return new updated quantity all products in {@link Catalog}
     */
    private Integer calculateNewQuantity(Product product, SupplierOrderItem supplierOrderItem) {
        return product.getCatalog().getQuantity() + supplierOrderItem.getQuantity();
    }
}
