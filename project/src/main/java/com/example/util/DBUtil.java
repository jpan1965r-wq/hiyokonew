package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    private static final Properties properties = new Properties();

    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                try (InputStream is2 = DBUtil.class.getResourceAsStream("/db.properties")) {
                    if (is2 != null) {
                        properties.load(is2);
                    } else {
                        throw new RuntimeException("db.properties file not found in classpath.");
                    }
                }
            }
            String driver = properties.getProperty("db.driver");
            if (driver != null && !driver.trim().isEmpty()) {
                Class.forName(driver.trim());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");
        if (url == null || url.trim().isEmpty()) {
            throw new SQLException("db.url property is null or empty.");
        }
        return DriverManager.getConnection(url, user, pass);
    }
}
