package com.epam.rd.controller;

import com.epam.rd.dto.ClientOrderDTO;
import com.epam.rd.entity.ClientOrder;
import com.epam.rd.entity.Payment;
import com.epam.rd.repository.ClientOrderRepository;
import com.epam.rd.service.impl.ClientOrderServiceImpl;
import com.epam.rd.service.impl.PaymentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    @Autowired
    private ClientOrderRepository clientOrderRepository;

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private ClientOrderServiceImpl clientOrderService;

    private Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping("/orderPayment")
    public void payClientOrder(@RequestParam ClientOrderDTO clientOrderDTO) {
        clientOrderRepository.findById(clientOrderDTO.cliendOrderId)
                .ifPresentOrElse(clientOrder -> {
                            Payment payment = createPayment(clientOrder, clientOrderDTO);
                            tryToPay(clientOrder, payment);
                        },
                        () -> logger.warn("Client order with id {} not found", clientOrderDTO.cliendOrderId));
    }

    private Payment createPayment(ClientOrder clientOrder, ClientOrderDTO clientOrderDTO) {
        Payment payment = new Payment();
        payment.setClientOrder(clientOrder);
        payment.setAmount(clientOrder.getClientOrderItems().size());
        payment.setInnClient(clientOrderDTO.innClient);
        payment.setKppClient(clientOrderDTO.kppClient);
        payment.setOgrnClient(clientOrderDTO.ogrnClient);
        payment.setPaymentAccountClient(clientOrderDTO.paymentAccountClient);
        return paymentService.create(payment);
    }

    private void tryToPay(ClientOrder clientOrder, Payment payment) {
        if (paymentService.pay(payment.getId())) {
            clientOrderService.markAsPaid(clientOrder.getId());
        } else {
            logger.warn("Cannot pay payment with id {}", payment.getId());
        }
    }
}
