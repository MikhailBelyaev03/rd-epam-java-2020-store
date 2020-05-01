package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "supplier_order_id")
    private UUID supplierOrderId;

    @Column(name = "client_order_id")
    private UUID clientOrderId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "payment")
    private List<SupplierOrder> supplierOrders = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "payment")
    private List<ClientOrder> clientOrders = new ArrayList<>();
}
