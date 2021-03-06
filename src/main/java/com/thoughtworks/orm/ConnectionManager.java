package com.thoughtworks.orm;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class ConnectionManager {
    public static int connectNumber = 0;
    private static Connection connection;

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

    public static DataSet getDataSet(String sqlQuery, Object... params) {
        return DataSet.createByResultSet(getResultSet(sqlQuery, params));
    }

    private static Connection connect() throws SQLException {
        try {
            Properties prop = new Properties();
            String file = ConnectionManager.class.getClassLoader().getResource("database.properties").getFile();
            FileReader reader = new FileReader(file);
            prop.load(reader);
            return getConnection((String) prop.get("connectionURL"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static ResultSet getResultSet(String sqlQuery, Object... params) {
        PreparedStatement statement;
        try {
            statement = getDBConnection().prepareStatement(sqlQuery);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
