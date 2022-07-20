package com.example.multitenancydemo.decorators;

import com.example.multitenancydemo.util.TenantContext;
import lombok.NonNull;
import org.springframework.core.task.TaskDecorator;
/*
    The TaskDecorator interface provides a mechanism to attach additional information to an asynchronous execution.
     This class is defined to pass the tenant id along
 */
public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        String tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                TenantContext.setTenantId(null);
            }
        };
    }
}