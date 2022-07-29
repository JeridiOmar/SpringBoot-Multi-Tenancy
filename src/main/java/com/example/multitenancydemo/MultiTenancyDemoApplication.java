package com.example.multitenancydemo;

import com.example.multitenancydemo.multitenancy.services.TenantManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultiTenancyDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiTenancyDemoApplication.class, args);
    }

}
