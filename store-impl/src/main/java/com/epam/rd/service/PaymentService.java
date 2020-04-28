package com.epam.rd.service;

import com.epam.rd.entity.Payment;
import java.util.UUID;

public interface PaymentService {

    Payment create(Payment payment);

    boolean pay(UUID paymentId);

    boolean sell(UUID paymentId);
}
