package com.example.multitenancydemo.multitenancy.services;

public interface TenantManagementService {
    void createTenant(String tenantId, String db, String password) throws Exception;
}
