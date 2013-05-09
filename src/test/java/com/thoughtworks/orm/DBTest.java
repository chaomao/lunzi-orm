package com.thoughtworks.orm;

import org.junit.After;
import org.junit.Before;

import java.sql.Connection;

public abstract class DBTest {
    protected static Connection connection;

    @Before
    public void before() throws Exception {
        connection = ConnectionManager.getDBConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.createStatement().execute("TRUNCATE author;");
        connection.createStatement().execute("TRUNCATE owner;");
        connection.createStatement().execute("TRUNCATE richowner;");
        connection.createStatement().execute("TRUNCATE house;");
        connection.createStatement().execute("TRUNCATE person;");
        connection.close();
    }
}
