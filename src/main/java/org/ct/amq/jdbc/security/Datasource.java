package org.ct.amq.jdbc.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Datasource for the jdbc security plugin
 *
 * @org.apache.xbean.XBean
 *
 */
public class Datasource {
    private String url;
    private String driver;
    private String user;
    private String password;

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Connection getConnection() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SecurityException("Driver class not found: " + driver);
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SecurityException("Cannot create connection to database", e);
        }
    }
}
