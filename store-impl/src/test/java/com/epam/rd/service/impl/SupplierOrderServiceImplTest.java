package com.epam.rd.service.impl;

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
import java.util.List;
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
    private SupplierOrderRepository supplierOrderRepository;

    @Mock
    private SupplierOrderItemRepository supplierOrderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SupplierOrderServiceImpl supplierOrderService = new SupplierOrderServiceImpl();

    @Test
    public void createTest() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder expectedSupplierOrder = new SupplierOrder();
        expectedSupplierOrder.setId(uuid);

        UUID uuidProduct = UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21");
        Product product = new Product();
        product.setId(uuidProduct);
        when(productRepository.findById(uuidProduct)).thenReturn(Optional.ofNullable(product));

        SupplierOrderItem supplierOrderItem = new SupplierOrderItem();
        supplierOrderItem.setProduct(product);
        supplierOrderItem.setSupplierOrder(expectedSupplierOrder);
        supplierOrderItem.setQuantity(100);

        expectedSupplierOrder.getSupplierOrderItems().add(supplierOrderItem);
        expectedSupplierOrder.setPrice(100.00);
        expectedSupplierOrder.setStatus("IN PROGRESS");
        expectedSupplierOrder.setPaymentId(UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99"));

        doNothing().when(supplierOrderItemRepository).save(supplierOrderItem);

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 100);
        SupplierOrder actualSupplierOrder = supplierOrderService.create(orderItem);
        actualSupplierOrder.setId(uuid);
        actualSupplierOrder.setPrice(100.00);

        assertEquals(expectedSupplierOrder.getSupplierOrderItems().size(), actualSupplierOrder.getSupplierOrderItems().size());
        List<SupplierOrderItem> list = expectedSupplierOrder.getSupplierOrderItems();
        List<SupplierOrderItem> actualList = actualSupplierOrder.getSupplierOrderItems();

        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(i).getId(), actualList.get(i).getId());
            assertEquals(list.get(i).getProduct(), actualList.get(i).getProduct());
            assertEquals(list.get(i).getSupplierOrder(), actualList.get(i).getSupplierOrder()); //Падает, если раскомментировать почему-то
            assertEquals(list.get(i).getQuantity(), actualList.get(i).getQuantity());
        }
    }

    @Test
    public void markAsDeliveredTest() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(Optional.ofNullable(supplierOrder));
        supplierOrderService.markAsDelivered(uuid);
        verify(supplierOrderRepository).findById(uuid);

        assertEquals(supplierOrder.getStatus(), "DELIVERED");
    }

    @Test
    public void checkOrderByPaymentIdTest() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setPaymentId(uuid);

        when(supplierOrderRepository.findByPaymentId(uuid)).thenReturn(Optional.ofNullable(supplierOrder));
        SupplierOrder actual = supplierOrderService.checkOrderByPaymentId(uuid);
        verify(supplierOrderRepository).findByPaymentId(uuid);

        assertEquals(actual.getId(), supplierOrder.getId());
    }
}