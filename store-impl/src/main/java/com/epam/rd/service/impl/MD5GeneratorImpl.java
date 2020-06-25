package com.epam.rd.service.impl;

import com.epam.rd.entity.Payment;
import com.epam.rd.service.MD5Generator;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class MD5GeneratorImpl implements MD5Generator {
    @Override
    public String MD5generate(Payment payment) {
        return String.join(":", md5Hex(payment.getOgrnShop()), md5Hex(payment.getInnShop()), md5Hex(payment.getKppShop()), md5Hex(payment.getPaymentAccountShop()));
    }
}
