package com.example.multitenancydemo.entity.metadata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tenant {

    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "db")
    private String db;

    @Column(name = "password")
    private String password;

    @Column(name = "url")
    private String url;

}