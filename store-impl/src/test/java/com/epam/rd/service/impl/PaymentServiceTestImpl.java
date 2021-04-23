package com.epam.rd.service.impl;

import com.epam.rd.Application;
import com.epam.rd.entity.ClientOrder;
import com.epam.rd.entity.Payment;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.repository.PaymentRepository;
import com.epam.rd.service.stub.PaymentStubService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class PaymentServiceTestImpl {

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private ClientOrderServiceImpl clientOrderService;

    @MockBean
    private PaymentStubService paymentStubService;

    @MockBean
    private SupplierOrderServiceImpl supplierOrderService;

    @InjectMocks
    @Autowired
    private PaymentServiceImpl paymentService;

    @Test
    public void createTest() {
        UUID uuid = UUID.fromString("e372a36c-c06a-4fcb-a476-91bae0e1c22a");
        Payment payment = new Payment();
        payment.setId(uuid);

        Payment actualPayment = paymentService.create(payment);

        verify(paymentRepository).save(payment);

        assertNotNull(actualPayment);
    }

    @Test
    public void payTest() {
        UUID uuid = UUID.fromString("10b8ebfb-c439-4fd8-a1b3-663734511975");
        Payment payment = new Payment();
        payment.setId(uuid);
        payment.setOgrnShop("1");
        payment.setKppShop("2");
        payment.setInnShop("3");
        payment.setPaymentAccountShop("4");

        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setId(uuid);

        when(paymentRepository.findBySupplierOrderId(uuid)).thenReturn(of(payment));
        when(paymentRepository.findById(paymentRepository.findBySupplierOrderId(uuid).get().getId())).thenReturn(of(payment));
        when(paymentStubService.send()).thenReturn(true);
        when(supplierOrderService.checkOrderByPaymentId(payment.getId())).thenReturn(supplierOrder);

        assertTrue(paymentService.pay(uuid));
    }

    @Test
    public void payTestWhenSupplierOrderNotFound() {
        UUID uuid = UUID.fromString("556d4c09-2ef0-4a59-b04f-aef4af78232f");
        SupplierOrder supplierOrder = new SupplierOrder();
        supplierOrder.setPaymentId(uuid);

        when(paymentRepository.findBySupplierOrderId(uuid)).thenReturn(empty());
        boolean actual = false;

        try {
            actual = paymentService.pay(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }
        assertFalse(actual);
    }

    @Test
    public void payTestWhenPaymentNotFound() {
        UUID uuid = UUID.fromString("e375a36c-c06a-4fcb-a476-91bae0e1c22a");
        Payment payment = new Payment();
        payment.setId(uuid);
        payment.setOgrnShop("1");
        payment.setKppShop("2");
        payment.setInnShop("3");
        payment.setPaymentAccountShop("4");

        when(paymentRepository.findBySupplierOrderId(uuid)).thenReturn(of(payment));
        when(paymentRepository.findById(paymentRepository.findBySupplierOrderId(uuid).get().getId())).thenReturn(empty());
        boolean actual = false;

        try {
            actual = paymentService.pay(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {

        }
        assertFalse(actual);
    }

    @Test
    public void sellTest() {
        UUID uuid = UUID.fromString("e372a36c-c06a-4fcb-a476-91bae0e1c22a");
        Payment payment = new Payment();
        payment.setId(uuid);
        payment.setOgrnShop("1");
        payment.setKppShop("2");
        payment.setInnShop("3");
        payment.setPaymentAccountShop("4");

        ClientOrder clientOrder = new ClientOrder();
        clientOrder.setId(UUID.fromString("e1712476-8127-11ea-a5f1-001e101f0000"));
        payment.setClientOrder(clientOrder);

        when(paymentRepository.findById(uuid)).thenReturn(of(payment));

        boolean actual = paymentService.sell(uuid);
        verify(clientOrderService).markAsPaid(payment.getClientOrder().getId());
        assertTrue(actual);
    }

    @Test
    public void sellTestWhenPaymentNotFound() {
        UUID uuid = UUID.fromString("556d4c09-2ef0-4a59-b04f-aef4af78232f");
        Payment payment = new Payment();
        payment.setId(uuid);

        when(paymentRepository.findById(uuid)).thenReturn(empty());
        boolean actual = false;

        try {
            actual = paymentService.sell(uuid);
            fail("No RuntimeException");
        } catch (RuntimeException e) {
        }
        assertFalse(actual);
    }
}
