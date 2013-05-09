package com.thoughtworks.orm;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class ConnectionManager {
    private static Connection connection;
    public static int connectNumber = 0;

    public static Connection getDBConnection() {
        connectNumber++;
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private static Connection connect() throws SQLException {
        return getConnection("jdbc:mysql://localhost:3306/orm?user=root");
    }
}
