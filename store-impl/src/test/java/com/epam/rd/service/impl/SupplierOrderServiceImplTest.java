package com.epam.rd.service.impl;

import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.entity.SupplierOrderItem;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderItemRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * @{code SupplierOrderServiceImplTest} testing work {@link SupplierOrderServiceImpl}
 *
 * @author Belousov Anton
 */
@RunWith(MockitoJUnitRunner.class)
public class SupplierOrderServiceImplTest {

    @Mock
    private SupplierOrderRepository supplierOrderRepository = new SupplierOrderRepository();

    @Mock
    private SupplierOrderItemRepository supplierOrderItemRepository = new SupplierOrderItemRepository();

    @Mock
    private ProductRepository productRepository = new ProductRepository();

    @InjectMocks
    private SupplierOrderServiceImpl supplierOrderService = new SupplierOrderServiceImpl();

    @Test
    public void createTest() {
        //UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder expectedSupplierOrder = new SupplierOrder();

        UUID uuidProduct = UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21");
        Catalog catalog = new Catalog();
        catalog.setPrice(1);
        catalog.setQuantity(11);
        Product product = new Product();
        product.setId(uuidProduct);
        product.setCatalog(catalog);

        SupplierOrderItem supplierOrderItem = new SupplierOrderItem();
        supplierOrderItem.setProduct(product);
        supplierOrderItem.setSupplierOrder(expectedSupplierOrder);
        supplierOrderItem.setQuantity(100);

        expectedSupplierOrder.getSupplierOrderItems().add(supplierOrderItem);
        expectedSupplierOrder.setPrice(1.0);
        expectedSupplierOrder.setStatus("IN PROGRESS");
        expectedSupplierOrder.setPaymentId(UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99"));

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 100);


        when(productRepository.findById(uuidProduct)).thenReturn(Optional.ofNullable(product));

        SupplierOrder actualSupplierOrder = supplierOrderService.create(orderItem);

        doNothing().when(supplierOrderItemRepository).save(supplierOrderItem);

        assertEquals(expectedSupplierOrder, actualSupplierOrder);
    }

    @Test
    public void markAsDeliveredTest() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(Optional.ofNullable(supplierOrder));
        supplierOrderService.markAsDelivered(uuid);
        verify(supplierOrderRepository).save(supplierOrder);

        assertEquals(supplierOrder.getStatus(), "DELIVERED");
    }

    @Test
    public void checkOrderByPaymentIdTest() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setPaymentId(uuid);

        when(supplierOrderRepository.findByPaymentId(uuid)).thenReturn(Optional.ofNullable(supplierOrder));
        SupplierOrder actual = supplierOrderService.checkOrderByPaymentId(uuid);

        assertEquals(actual.getId(), supplierOrder.getId());
    }
}