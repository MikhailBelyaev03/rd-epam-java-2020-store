package com.epam.rd.service.impl;

import com.epam.rd.entity.Payment;
import com.epam.rd.entity.SupplierOrder;
import com.epam.rd.repository.PaymentRepository;
import com.epam.rd.service.PaymentService;
import com.epam.rd.service.stub.PaymentStubService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static java.util.Optional.ofNullable;


@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository paymentRepository = new PaymentRepository();
    private PaymentStubService paymentStubService = new PaymentStubService();
    private SupplierOrderServiceImpl supplierOrderService = new SupplierOrderServiceImpl();
    private ClientOrderServiceImpl clientOrderService = new ClientOrderServiceImpl();
    private MD5GeneratorImpl md5Generator = new MD5GeneratorImpl();

    /**
     * Create payment in database
     *
     * @param payment
     * @return payment in Payment
     */
    @Override
    public Payment create(Payment payment) {
        paymentRepository.save(payment);
        return payment;
    }

    /**
     * Pay method
     *
     * @param paymentId
     * @return true of false
     */
    @Override
    public boolean pay(UUID paymentId) {
        SupplierOrder supplierOrder = ofNullable(supplierOrderService.checkOrderByPaymentId(paymentId)).orElseThrow(() -> {
            log.warn("Supplier order with payment id = {} is not exists", paymentId);
            return new RuntimeException("Supplier order not found");
        });
        Payment payment = paymentRepository.findBySupplierOrderId(supplierOrder.getId()).orElseThrow(() -> {
            log.warn("Payment with supplier_order_id = {} is not exists", supplierOrder.getId());
            return new RuntimeException("Payment is not found");
        });
        md5Generator.MD5generate(payment);
        paymentStubService.send();
        return true;
    }


    /**
     * Sell method
     *
     * @param paymentId
     * @return true of false
     */
    @Override
    public boolean sell(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> {
            log.warn("Payment with id = {} is not exists", paymentId);
            return new RuntimeException("Payment is not found");
        });
        md5Generator.MD5generate(payment);
        clientOrderService.markAsPaid(payment.getClientOrder().getId());
        return true;
    }
}