package com.epam.rd.service.impl;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import com.epam.rd.Application;
import com.epam.rd.entity.ClientOrder;
import com.epam.rd.entity.Payment;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.repository.PaymentRepository;
import com.epam.rd.service.stub.PaymentStubService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = Application.class)
public class PaymentServiceImplTest {

    private static final String STATUS_PAID = "PAID";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private PaymentStubService paymentStubService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ClientOrderServiceImpl clientOrderService;

    @Mock
    private SupplierOrderServiceImpl supplierOrderService;

    @InjectMocks
    @Autowired
    private PaymentServiceImpl paymentService;

    @Mock
    private MD5GeneratorImpl md5Generator = new MD5GeneratorImpl();

    @Test
    public void createWhenPaymentNotNullThenCreate() {
        Payment expectedPayment = new Payment();
        UUID UUIDPayment = UUID.randomUUID();
        expectedPayment.setId(UUIDPayment);
        when(paymentRepository.save(expectedPayment)).thenReturn(expectedPayment);
        Payment actualPayment = paymentService.create(expectedPayment);
        assertEquals(expectedPayment, actualPayment);
    }

    @Test
    public void payWhenOrderNotFoundedThenThrowExceptionByOrder() {

        UUID UUIDPayment = UUID.randomUUID();
        String expectedException = "Supplier order not found";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        paymentService.pay(UUIDPayment);

    }

    @Test
    public void payWhenOrderNotFoundedThenThrowExceptionByPayment() {
        SupplierOrder expectedOrder = new SupplierOrder();
        UUID UUIDOrder = UUID.randomUUID();
        expectedOrder.setId(UUIDOrder);
        expectedOrder.setPrice(100);
        expectedOrder.setStatus("Not Paid");

        UUID expectedUUID = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(expectedUUID);
        expectedOrder.setPaymentId(expectedUUID);

        doReturn(expectedOrder).when(supplierOrderService).checkOrderByPaymentId(expectedUUID);

        String expectedException = "Payment is not found";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        paymentService.pay(expectedUUID);

    }

    @Test
    public void payWhenOrderAndPaymentFoundedThenPay() {
        SupplierOrder expectedOrder = new SupplierOrder();
        UUID UUIDOrder = UUID.randomUUID();
        expectedOrder.setId(UUIDOrder);
        expectedOrder.setPrice(100);
        expectedOrder.setStatus("Not Paid");

        UUID expectedUUID = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(expectedUUID);
        expectedOrder.setPaymentId(expectedUUID);

        doReturn(of(payment)).when(paymentRepository).findBySupplierOrderId(UUIDOrder);
        doReturn(expectedOrder).when(supplierOrderService).checkOrderByPaymentId(expectedUUID);

        doReturn("").when(md5Generator).MD5generate(payment);
        paymentService.pay(expectedUUID);

    }

    @Test
    public void sellWhenPaymentNotFoundedThenThrowException() {
        UUID expectedUUID = UUID.randomUUID();
        when(paymentRepository.findById(expectedUUID))
                .thenReturn(empty());
        paymentRepository.findById(expectedUUID);

        String expectedException = "Payment is not found";
        exception.expect(RuntimeException.class);
        exception.expectMessage(expectedException);

        paymentService.sell(expectedUUID);
    }

    @Test
    public void sellWhenPaymentFoundedThenMarkIt() {

        UUID expectedUUID = UUID.randomUUID();
        Payment expectedPayment = new Payment();
        expectedPayment.setId(expectedUUID);
        ClientOrder expectedOrder = new ClientOrder();
        expectedOrder.setPaymentId(expectedUUID);
        UUID UUIDOrder = UUID.randomUUID();
        expectedOrder.setId(UUIDOrder);
        expectedPayment.setClientOrder(expectedOrder);

        when(paymentRepository.findById(expectedUUID)).thenReturn(of(expectedPayment));

        paymentService.sell(expectedUUID);
    }

}