package com.epam.rd.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;;
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

    private String ogrnShop;

    private String kppShop;

    private String innShop;

    private String paymentAccountShop;

    private String ogrnClient;

    private String kppClient;

    private String innClient;

    private String paymentAccountClient;

    private String key;

    private int amount;

    private String callbackUrl;

    private String status;

    private UUID supplierOrderId;

    private UUID clientOrderId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "payment")
    private List<SupplierOrder> supplierOrders = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "payment")
    private List<ClientOrder> clientOrders = new ArrayList<>();
}
