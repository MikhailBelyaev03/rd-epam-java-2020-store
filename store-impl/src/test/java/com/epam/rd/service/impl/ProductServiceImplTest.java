package com.epam.rd.service.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.epam.rd.Application;
import com.epam.rd.entity.Catalog;
import com.epam.rd.entity.Product;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.repository.CatalogRepository;
import com.epam.rd.repository.ProductRepository;
import com.epam.rd.repository.SupplierOrderRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = Application.class)
public class ProductServiceImplTest {

    private static final String SUPPLIER_ORDER_STATUS_DELIVERED = "DELIVERED";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierOrderRepository supplierOrderRepository;

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    @Autowired
    private ProductServiceImpl productService;

    @Mock
    private SupplierOrderServiceImpl supplierOrderService;

    @Test
    public void calculateStockWhenRepositoryIsNotEmpty() {
        List<Product> productList = new ArrayList<>();
        Product product = new Product();
        UUID UUIDProduct = UUID.randomUUID();
        product.setId(UUIDProduct);
        Catalog catalog = new Catalog();
        catalog.setQuantity(200);
        product.setCatalog(catalog);


        productList.add(product);
        Map<UUID, Integer> productMap = productList.stream().filter(o -> o.getCatalog().getQuantity() > 1).collect(Collectors.toMap(Product::getId, o -> o.getCatalog().getQuantity()));

        supplierOrderService.create(productMap);
    }

    @Test
    public void calculateStockWhenProductListIsEmpty() {

        List<Product> productList = (List<Product>) productRepository.findAll();
        Product product = new Product();
        UUID UUIDProduct = UUID.randomUUID();
        product.setId(UUIDProduct);
        Catalog catalog = new Catalog();
        catalog.setQuantity(20);
        product.setCatalog(catalog);

        productList.add(product);

        Map<UUID, Integer> productMap = productList.stream().collect(Collectors.toMap(Product::getId, o -> o.getCatalog().getQuantity()));

        supplierOrderService.create(productMap);
        productService.calculateStock();
    }

    @Test
    public void receiveDeliveryWhenSupplierOrderNullThenThrowException() {
        UUID expectedUUID = UUID.randomUUID();
        when(supplierOrderRepository.findById(expectedUUID))
                .thenReturn(empty());

        String expectedException = "Supplier order not found by supplier order id";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        productService.receiveDelivery(expectedUUID);
    }

    @Test
    public void receiveDeliveryWhenSupplierOrderNotNullThenMarkAsDelivered() {

        UUID expectedUUID = UUID.randomUUID();
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(expectedUUID);
        supplierOrder.setStatus("FAIL");
        when(supplierOrderRepository.findById(expectedUUID)).thenReturn(of(supplierOrder));
        supplierOrderRepository.findById(expectedUUID);
        verify(supplierOrderRepository).findById(expectedUUID);

        productService.receiveDelivery(expectedUUID);
    }


    @Test
    public void reserveProductWhenProductNotFoundedThenThrowException() {
        UUID expectedUUID = UUID.randomUUID();

        String expectedException = "Product not found by product id";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        productRepository.findById(expectedUUID);
        productService.reserveProduct(expectedUUID, 100);

    }

    @Test
    public void reserveProductWhenProductIsFoundedThenSaveToTheRepository() {
        Catalog actualCatalog = new Catalog();
        UUID UUIDCatalog = UUID.randomUUID();
        actualCatalog.setId(UUIDCatalog);

        UUID UUIDProduct = UUID.randomUUID();
        Product expectedProduct = new Product();
        expectedProduct.setId(UUIDProduct);
        expectedProduct.setCatalog(actualCatalog);
        expectedProduct.getCatalog().setQuantity(15);

        when(productRepository.findById(UUIDProduct)).thenReturn(of(expectedProduct));
        productService.reserveProduct(UUIDProduct, 10);
        verify(catalogRepository).save(actualCatalog);
    }

    @Test
    public void reserveProductWhenQuantityProductNotEnoughThenThrowException() {

        UUID expectedUUID = UUID.randomUUID();
        Product product = new Product();
        product.setId(expectedUUID);
        Catalog catalog = new Catalog();
        catalog.setQuantity(100);
        product.setCatalog(catalog);
        when(productRepository.findById(expectedUUID)).thenReturn(of(product));

        String expectedException = "The quantity of goods in stock is less than necessary for the order";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        productService.reserveProduct(expectedUUID, 10000);

    }

}