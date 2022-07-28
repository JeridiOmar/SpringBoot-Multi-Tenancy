package com.example.multitenancydemo.services;

public interface TenantManagementService {
    void createTenant(String tenantId, String db, String password) throws Exception;
}
