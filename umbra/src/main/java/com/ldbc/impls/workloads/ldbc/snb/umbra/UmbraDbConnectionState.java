package com.ldbc.impls.workloads.ldbc.snb.umbra;

import com.ldbc.driver.DbException;
import com.ldbc.impls.workloads.ldbc.snb.BaseDbConnectionState;
import com.ldbc.impls.workloads.ldbc.snb.QueryStore;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

public class UmbraDbConnectionState<TDbQueryStore extends QueryStore> extends BaseDbConnectionState<TDbQueryStore> {

    protected String endPoint;
    protected HikariDataSource ds;
    protected Connection connection;

    public UmbraDbConnectionState(Map<String, String> properties, TDbQueryStore store) throws ClassNotFoundException {
        super(properties, store);
        endPoint = properties.get("endpoint");

        Properties props = new Properties();
        endPoint = properties.get("endpoint");
        props.setProperty("jdbcUrl", endPoint);
        props.setProperty("dataSource.databaseName", properties.get("databaseName"));
        props.setProperty("dataSource.assumeMinServerVersion", "9.0");
        props.setProperty("dataSource.ssl", "false");

        HikariConfig config = new HikariConfig(props);
        config.setPassword(properties.get("password"));
        config.setUsername(properties.get("user"));
        config.setJdbcUrl(endPoint);
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws DbException {
        try {
            if (connection == null) {
                TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
                connection = ds.getConnection();
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
