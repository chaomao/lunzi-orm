package com.thoughtworks.orm;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class Model {
    protected int id;

    public void save() {
        PreparedStatement statement = null;
        try {
            String query = QueryGenerator.insert(this);
            statement = ConnectionManager.getDBConnection().prepareStatement(query, new String[]{"id"});
            setAttributeValuesIntoStatement(statement);
            statement.executeUpdate();
            updateId(statement);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private void setAttributeValuesIntoStatement(PreparedStatement statement) throws SQLException, IllegalAccessException {
        Iterable<Field> attributes = ModelHelper.getAttributes(this);
        int index = 1;
        for (Field field : attributes) {
            statement.setObject(index++, createAttributeValue(field));
        }
    }

    private Object createAttributeValue(Field field) throws IllegalAccessException {
        field.setAccessible(true);
        Object value = field.get(this);
        return field.getType().isEnum() ? value.toString() : value;
    }

    private void updateId(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        id = generatedKeys.getInt(1);
    }

    public int getId() {
        return id;
    }
}