package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.Column;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ORMModel {
    @Column
    protected int id;

    public void save() {
        PreparedStatement statement = null;
        try {
            String query = QueryGenerator.insert(this);
            Iterable<Field> attributes1 = ModelHelper.getAttributes(this);
            statement = ConnectionManager.getDBConnection().prepareStatement(query, new String[]{"id"});
            int index = 1;
            for (Field field : attributes1) {
                field.setAccessible(true);
                statement.setObject(index++, field.get(this));
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            id = generatedKeys.getInt(1);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }
    }

}