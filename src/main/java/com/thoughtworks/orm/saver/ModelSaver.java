package com.thoughtworks.orm.saver;

import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;
import com.thoughtworks.orm.QueryGenerator;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ModelSaver {
    protected Model model;

    public ModelSaver(Model model) {
        this.model = model;
    }

    public void save() {
        PreparedStatement statement = null;
        try {
            String query = QueryGenerator.insertQuery(getModel(), getAttributesForInsert());
            statement = prepareStatement(query);
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

    private Model getModel() {
        return model;
    }

    protected Iterable<Field> getAttributesForInsert() {
        return ModelHelper.getAttributesForInsert(getModel());
    }

    protected void saveAssociations() {
        for (Field field : ModelHelper.getHasOneAssociationFields(getModel())) {
            try {
                Model associationModel = (Model) field.get(getModel());
                ModelWithParentModelSaver saver = new ModelWithParentModelSaver(associationModel, getModel(), field);
                saver.save();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected PreparedStatement prepareStatement(String query) throws SQLException {
        return ConnectionManager.getDBConnection().prepareStatement(query, new String[]{"id"});
    }

    protected void setAttributeValuesIntoStatement(PreparedStatement statement) throws SQLException, IllegalAccessException {
        Iterable<Field> attributes = getAttributesForInsert();
        int index = 1;
        for (Field field : attributes) {
            statement.setObject(index++, createAttributeValue(field));
        }
    }

    protected Object createAttributeValue(Field field) throws IllegalAccessException {
        Object value = field.get(getModel());
        Class<?> type = field.getType();
        if (type.isEnum()) {
            return value.toString();
        } else if (type.equals(ArrayList.class)) {
            return createArrayListValue((ArrayList) value);
        } else return value;
    }

    private Object createArrayListValue(ArrayList value) {
        StringBuilder sb = new StringBuilder();
        for (Object o : value) {
            sb.append(o.toString());
            sb.append("--");
        }
        return sb.toString();
    }

    protected void updateSelfId(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        getModel().setId(generatedKeys.getInt(1));
    }
}
