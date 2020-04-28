package com.epam.rd.service.impl;

import com.epam.rd.entity.Payment;
import com.epam.rd.service.PaymentService;
import java.util.UUID;

public class PaymentServiceImpl implements PaymentService {

    @Override
    public Payment create(Payment payment) {
        return null;
    }

    @Override
    public boolean pay(UUID paymentId) {
        return false;
    }

    @Override
    public boolean sell(UUID paymentId) {
        return false;
    }
}
