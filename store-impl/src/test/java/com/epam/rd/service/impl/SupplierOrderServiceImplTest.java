package com.epam.rd.service.impl;

import com.epam.rd.Application;
import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.entity.SupplierOrderItem;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderItemRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import com.epam.rd.service.stub.SupplierStubService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * @{code SupplierOrderServiceImplTest} testing work {@link SupplierOrderServiceImpl}
 *
 * @author Belousov Anton
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SupplierOrderServiceImplTest {

    @MockBean
    private SupplierOrderRepository supplierOrderRepository;

    @MockBean
    private SupplierOrderItemRepository supplierOrderItemRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private SupplierStubService supplierStubService;

    @InjectMocks
    @Autowired
    private SupplierOrderServiceImpl supplierOrderService;

    @Test
    public void testCreateWhenTheProductExists() {
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
        expectedSupplierOrder.setPrice(100.0);
        expectedSupplierOrder.setStatus("IN PROGRESS");

        when(supplierStubService.send()).thenReturn(UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99"));

        expectedSupplierOrder.setPaymentId(supplierStubService.send());

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 100);


        when(productRepository.findById(uuidProduct)).thenReturn(of(product));



        SupplierOrder actualSupplierOrder = supplierOrderService.create(orderItem);

        verify(supplierOrderRepository).save(any(SupplierOrder.class));
        verify(productRepository).findById(uuidProduct);

        assertEquals(expectedSupplierOrder, actualSupplierOrder);
    }

    @Test
    public void testCreateWhenTheProductNotExists() {
        UUID uuidProduct = UUID.fromString("0afd3797-f753-4aed-94a1-0c7a0a053d21");
        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(uuidProduct, 100);

        when(productRepository.findById(uuidProduct)).thenReturn(empty());
        SupplierOrder actualSupplierOrder = null;
        try {
            actualSupplierOrder = supplierOrderService.create(orderItem);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        verify(supplierOrderRepository, never()).save(any(SupplierOrder.class));
        verify(productRepository).findById(uuidProduct);

        assertNull(actualSupplierOrder);
    }

    @Test
    public void testMarkAsDeliveredWhenTheOrderExists() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(of(supplierOrder));
        supplierOrderService.markAsDelivered(uuid);
        verify(supplierOrderRepository).save(supplierOrder);

        assertEquals(supplierOrder.getStatus(), "DELIVERED");
    }

    @Test
    public void testMarkAsDeliveredWhenTheOrderIsNotFound() {
        UUID uuid = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(supplierOrderRepository.findById(uuid)).thenReturn(empty());

        try {
            supplierOrderService.markAsDelivered(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        verify(supplierOrderRepository, never()).save(supplierOrder);
        assertNull(supplierOrder.getStatus());
    }

    @Test
    public void testCheckOrderByPaymentIdWhenTheOrderExists() {
        UUID paymentId = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setPaymentId(paymentId);

        when(supplierOrderRepository.findByPaymentId(paymentId)).thenReturn(of(supplierOrder));
        SupplierOrder actual = supplierOrderService.checkOrderByPaymentId(paymentId);

        assertEquals(actual.getPaymentId(), supplierOrder.getPaymentId());
    }

    @Test
    public void testCheckOrderByPaymentIdWhenTheOrderNotFound() {
        UUID paymentId = UUID.fromString("96d989e7-3d64-4e72-ab06-b3ec52f31f99");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setPaymentId(paymentId);

        when(supplierOrderRepository.findByPaymentId(paymentId)).thenReturn(empty());

        SupplierOrder actual = null;
        try {
            actual = supplierOrderService.checkOrderByPaymentId(paymentId);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }

        assertNull(actual);
    }
}