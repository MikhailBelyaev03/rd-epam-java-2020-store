package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import java.util.UUID;
@Data
@NoArgsConstructor
@Entity
@Table(name = "st_payment")
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "ogrn_shop")
    private String ogrnShop;

    @Column(name = "kpp_shop")
    private String kppShop;

    @Column(name = "inn_shop")
    private String innShop;

    @Column(name = "payment_account_shop")
    private String paymentAccountShop;

    @Column(name = "ogrn_client")
    private String ogrnClient;

    @Column(name = "kpp_client")
    private String kppClient;

    @Column(name = "inn_client")
    private String innClient;

    @Column(name = "payment_account_client")
    private String paymentAccountClient;

    private String key;

    private int amount;

    @Column(name = "callback_url")
    private String callbackUrl;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_order_id")
    private SupplierOrder supplierOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_order_id")
    private ClientOrder clientOrder;
}
