package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class Model {
    protected int id;

    public void save() {
        PreparedStatement statement = null;
        try {
            String query = QueryGenerator.insertQuery(this, ModelHelper.getAttributesForInsert(this));
            statement = ConnectionManager.getDBConnection().prepareStatement(query, new String[]{"id"});
            setAttributeValuesIntoStatement(statement);
            statement.executeUpdate();
            updateSelfId(statement);
            saveAssociations();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException ignored) {
            }
        }
    }

    protected void saveAssociations() {
        for (Field field : ModelHelper.getHasOneAssociationFields(this)) {
            try {
                Model association = (Model) field.get(this);
                association.saveWithParent(this, field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveWithParent(Model parent, Field associatedField) {
        PreparedStatement statement;
        try {
            List<Field> attributes = Lists.newArrayList(ModelHelper.getAttributesForInsert(this));
            attributes.add(associatedField);
            String query = QueryGenerator.insertQuery(this, attributes);
            statement = ConnectionManager.getDBConnection().prepareStatement(query, new String[]{"id"});
            int index = 1;
            for (Field field : attributes) {
                Object value;
                if (field.isAnnotationPresent(HasOne.class)) {
                    value = parent.getId();
                } else {
                    value = createAttributeValue(field);
                }
                statement.setObject(index++, value);
            }
            statement.executeUpdate();
            updateSelfId(statement);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setAttributeValuesIntoStatement(PreparedStatement statement) throws SQLException, IllegalAccessException {
        Iterable<Field> attributes = ModelHelper.getAttributesForInsert(this);
        int index = 1;
        for (Field field : attributes) {
            statement.setObject(index++, createAttributeValue(field));
        }
    }

    private Object createAttributeValue(Field field) throws IllegalAccessException {
        Object value = field.get(this);
        return field.getType().isEnum() ? value.toString() : value;
    }

    private void updateSelfId(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        id = generatedKeys.getInt(1);
    }

    public int getId() {
        return id;
    }
}