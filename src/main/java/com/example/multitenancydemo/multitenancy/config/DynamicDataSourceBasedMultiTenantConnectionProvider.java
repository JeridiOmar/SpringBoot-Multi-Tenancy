package com.example.multitenancydemo.multitenancy.config;

import com.example.multitenancydemo.multitenancy.entity.Tenant;
import com.example.multitenancydemo.multitenancy.repositories.TenantRepository;
import com.example.multitenancydemo.util.EncryptionService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
/*
     stores the dataSources in a LoadingCache which creates a new dataSource for a tenant on first access and evicts and closes dataSources for tenants which hasn’t been active for a while

 */
public class DynamicDataSourceBasedMultiTenantConnectionProvider
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final String TENANT_POOL_NAME_SUFFIX = "DataSource";

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    @Qualifier("masterDataSource")
    private DataSource masterDataSource;

    @Autowired
    @Qualifier("masterDataSourceProperties")
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private TenantRepository masterTenantRepository;

    @Value("${multitenancy.datasource-cache.maximumSize:100}")
    private Long maximumSize;

    @Value("${multitenancy.datasource-cache.expireAfterAccess:10}")
    private Integer expireAfterAccess;

    @Value("${encryption.secret}")
    private String secret;

    @Value("${encryption.salt}")
    private String salt;

    private LoadingCache<String, DataSource> tenantDataSources;

    @PostConstruct
    private void createCache() {
        tenantDataSources = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, DataSource>) removal -> {
                    HikariDataSource ds = (HikariDataSource) removal.getValue();
                    ds.close(); // tear down properly
                    log.info("Closed datasource: {}", ds.getPoolName());
                })
                .build(new CacheLoader<String, DataSource>() {
                    public DataSource load(String key) {
                        Tenant tenant = masterTenantRepository.findByTenantId(key)
                                .orElseThrow(() -> new RuntimeException("No such tenant: " + key));
                        return createAndConfigureDataSource(tenant);
                    }
                });
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return masterDataSource;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        try {
            return tenantDataSources.get(tenantIdentifier);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to load DataSource for tenant: " + tenantIdentifier);
        }
    }

    private DataSource createAndConfigureDataSource(Tenant tenant) {
        String decryptedPassword = encryptionService.decrypt(tenant.getPassword(), secret, salt);

        HikariDataSource ds = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        ds.setUsername(tenant.getDb());
        ds.setPassword(decryptedPassword);
        ds.setJdbcUrl(tenant.getUrl());

        ds.setPoolName(tenant.getTenantId() + TENANT_POOL_NAME_SUFFIX);

        log.info("Configured datasource: {}", ds.getPoolName());
        return ds;
    }

}
