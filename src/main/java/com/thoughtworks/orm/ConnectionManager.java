package com.thoughtworks.orm;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class ConnectionManager {
    private static Connection connection;

    public static Connection getDBConnection() {
        if (connection == null) {
            connection = connect();
        }
        return connection;
    }

    private static Connection connect() {
        try {
            return getConnection("jdbc:mysql://localhost:3306/orm?user=root");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
