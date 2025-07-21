package org.example.server.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库工具类
 * 使用HikariCP实现数据库连接池
 */
public class DatabaseUtil {
    // 数据库连接配置
    private static final String URL = "jdbc:mysql://106.14.167.110:3306/chamber_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "sfsgegdd123";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // 连接池配置
    private static final int MAX_POOL_SIZE = 10;
    private static final int MIN_IDLE = 5;
    private static final long IDLE_TIMEOUT = 600000; // 10分钟
    private static final long MAX_LIFETIME = 1800000; // 30分钟
    private static final long CONNECTION_TIMEOUT = 30000; // 30秒

    private static HikariDataSource dataSource;

    static {
        init();
    }

    public static void init() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setDriverClassName(DRIVER_CLASS);

            // 连接池大小
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);

            // 超时设置
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            config.setConnectionTimeout(CONNECTION_TIMEOUT);

            // 初始化连接池
            dataSource = new HikariDataSource(config);

            System.out.println("Database connection pool initialized successfully");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            throw e;
        }
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }
}
