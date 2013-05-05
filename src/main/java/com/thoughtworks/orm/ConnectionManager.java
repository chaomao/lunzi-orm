package com.thoughtworks.orm;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class ConnectionManager {
    private static Connection connection;

    public static Connection getDBConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
