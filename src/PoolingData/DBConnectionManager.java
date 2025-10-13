package PoolingData;



import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionManager {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlserver://localhost:1433;databaseName=nchdbase;encrypt=false;trustServerCertificate=true");
        config.setUsername("admin");
        config.setPassword("yeyel2025");

        // pool settings
        config.setMaximumPoolSize(10); // up to 10 concurrent connections
        config.setMinimumIdle(2); // keep 2 connections ready
        config.setIdleTimeout(300000); // 5 minutes idle timeout
        config.setConnectionTimeout(5000); // wait max 5 sec for connection

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
