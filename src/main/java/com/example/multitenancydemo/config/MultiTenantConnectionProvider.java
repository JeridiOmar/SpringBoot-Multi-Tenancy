package com.example.multitenancydemo.config;

import org.hibernate.service.Service;
import org.hibernate.service.spi.Wrapped;

import java.sql.Connection;
import java.sql.SQLException;
/*
    responsibility is to provide tenant-aware JDBC connections.
 */
public interface MultiTenantConnectionProvider extends Service, Wrapped {
    /**
     * Allows access to the database metadata of the underlying database(s) in situations where we do not have a
     * tenant id (like startup processing, for example).
     */
    public Connection getAnyConnection() throws SQLException;

    /**
     * Release a connection obtained from {@link #getAnyConnection}
     */
    public void releaseAnyConnection(Connection connection) throws SQLException;

    /**
     * Obtains a connection for Hibernate use according to the underlying strategy of this provider.
     *
     * @param tenantIdentifier The identifier of the tenant for which to get a connection
     */
    public Connection getConnection(String tenantIdentifier) throws SQLException;

    /**
     * Release a connection from Hibernate use.
     */
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException;

    /**
     * Does this connection provider support aggressive release of JDBC
     * connections and re-acquisition of those connections (if need be) later?
     */
    public boolean supportsAggressiveRelease();
}