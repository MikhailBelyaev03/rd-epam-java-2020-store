package com.epam.rd.service;

import com.epam.rd.entity.Payment;

public interface MD5Generator {
    String MD5generate(Payment payment);
}
