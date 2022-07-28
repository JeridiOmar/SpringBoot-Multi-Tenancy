package com.example.multitenancydemo.multitenancy.controller;

import com.example.multitenancydemo.multitenancy.services.TenantManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class TenantsApiController {

    @Autowired
    private TenantManagementService tenantManagementService;

    @PostMapping("/tenants")
    public ResponseEntity<Void> createTenant(@RequestParam String tenantId, @RequestParam String db, @RequestParam String password) throws Exception {
        tenantManagementService.createTenant(tenantId, db, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}