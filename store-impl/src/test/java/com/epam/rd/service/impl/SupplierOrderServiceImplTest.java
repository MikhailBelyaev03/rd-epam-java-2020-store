package com.epam.rd.service.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.rd.Application;
import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.entity.SupplierOrderItem;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SupplierOrderServiceImplTest {

    private static final String SUPPLIER_ORDER_STATUS_IN_PROGRESS = "IN PROGRESS";

    private static final String SUPPLIER_ORDER_STATUS_DELIVERED = "DELIVERED";

    private static final String SUPPLIER_ORDER_PAYMENT_CALLBACK_URL = null;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @MockBean
    private SupplierOrderRepository supplierOrderRepository;

    @MockBean
    private ProductRepository productRepository;


    @InjectMocks
    @Autowired
    private SupplierOrderServiceImpl supplierOrderService;

    @Test
    public void createWhenSupplierOrderIsNotNullCreateOrder() {
        UUID UUIDProduct = UUID.randomUUID();
        Product product = new Product();
        product.setId(UUIDProduct);

        Catalog catalog = new Catalog();
        catalog.setPrice(10);
        catalog.setQuantity(1);
        product.setCatalog(catalog);

        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(UUIDProduct, 100);

        when(productRepository.findById(UUIDProduct)).thenReturn(of(product));

        SupplierOrder actualSupplierOrder = supplierOrderService.create(orderItem);

        verify(supplierOrderRepository).save(any(SupplierOrder.class));
        verify(productRepository).findById(UUIDProduct);

        assertNotNull(actualSupplierOrder);
    }

    @Test
    public void createWhenProductNotFoundThenThrowException() {
        SupplierOrder expectedSupplierOrder = new SupplierOrder();

        expectedSupplierOrder.setStatus(SUPPLIER_ORDER_STATUS_IN_PROGRESS);
        expectedSupplierOrder.setPaymentCallbackUrl(SUPPLIER_ORDER_PAYMENT_CALLBACK_URL);

        List<SupplierOrderItem> supplierOrderItemList = new ArrayList<>();
        expectedSupplierOrder.setSupplierOrderItems(supplierOrderItemList);
        expectedSupplierOrder.setPrice(10);
        expectedSupplierOrder.setPaymentId(UUID.randomUUID());

        UUID expectedUUID = UUID.randomUUID();
        Map<UUID, Integer> orderItem = new HashMap<>();
        orderItem.put(expectedUUID, 100);
        try {
            supplierOrderService.create(orderItem);
        } catch (RuntimeException re) {
            String message = "Product not found by product id";
            assertEquals(message, re.getMessage());
        }
    }

    @Test
    public void markAsDeliveredWhenOrderIsFoundedThenMarkIt() {
        UUID expectedUUID = UUID.randomUUID();
        SupplierOrder expectedSupplierOrder = new SupplierOrder();
        when(supplierOrderRepository.findById(expectedUUID)).thenReturn(of(expectedSupplierOrder));
        SupplierOrder actualSupplierOrder = supplierOrderRepository.findById(expectedUUID).get();

        actualSupplierOrder.setStatus(SUPPLIER_ORDER_STATUS_DELIVERED);
        supplierOrderService.markAsDelivered(expectedUUID);
        verify(supplierOrderRepository).save(actualSupplierOrder);
        assertEquals(SUPPLIER_ORDER_STATUS_DELIVERED, actualSupplierOrder.getStatus());
    }

    @Test
    public void markAsDeliveredWhenOrderIsNotFoundedThenThrownException() {
        UUID expectedUUID = UUID.randomUUID();
        when(supplierOrderRepository.findById(expectedUUID))
                .thenReturn(empty());
        try {
            supplierOrderService.markAsDelivered(expectedUUID);
        } catch (RuntimeException re) {
            String message = "Order for supplier not found by supplier order id";
            assertEquals(message, re.getMessage());
        }
    }

    @Test
    public void checkOrderByPaymentIdWhenOrderIsNullThenReturnNull() {
        UUID actualUUID = UUID.randomUUID();
        SupplierOrder expectedOrder = new SupplierOrder();
        expectedOrder.setPaymentId(actualUUID);

        when(supplierOrderRepository.findByPaymentId(actualUUID))
                .thenReturn(empty());
        try {
            supplierOrderService.checkOrderByPaymentId(actualUUID);
        } catch (RuntimeException re) {
            String message = "Supplier order not found by payment id";
            assertEquals(message, re.getMessage());
        }

    }

    @Test
    public void checkOrderByPaymentIdWhenOrderIsNotNullThenReturnOrder() {
        UUID actualUUID = UUID.randomUUID();
        SupplierOrder expectedOrder = new SupplierOrder();
        expectedOrder.setPaymentId(actualUUID);

        when(supplierOrderRepository.findByPaymentId(actualUUID))
                .thenReturn(of(expectedOrder));
        SupplierOrder actualSupplierOrder = supplierOrderService.checkOrderByPaymentId(actualUUID);
        assertSame(expectedOrder, actualSupplierOrder);

    }
}