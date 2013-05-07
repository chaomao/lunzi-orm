package com.thoughtworks.orm.saver;

import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;
import com.thoughtworks.orm.QueryGenerator;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModelSaver extends Saver {

    public ModelSaver(Model model) {
        super(model);
    }

    @Override
    public void save() {
        PreparedStatement statement = null;
        try {
            String query = QueryGenerator.insertQuery(getModel(), ModelHelper.getAttributesForInsert(getModel()));
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

    private void setAttributeValuesIntoStatement(PreparedStatement statement) throws SQLException, IllegalAccessException {
        Iterable<Field> attributes = ModelHelper.getAttributesForInsert(getModel());
        int index = 1;
        for (Field field : attributes) {
            statement.setObject(index++, createAttributeValue(field));
        }
    }

    private Object createAttributeValue(Field field) throws IllegalAccessException {
        Object value = field.get(getModel());
        return field.getType().isEnum() ? value.toString() : value;
    }

    protected void saveAssociations() {
        for (Field field : ModelHelper.getHasOneAssociationFields(getModel())) {
            try {
                Model associationModel = (Model) field.get(getModel());
                ModelWithParentSaver saver = new ModelWithParentSaver(associationModel, getModel(), field);
                saver.save();
//                associationModel.saveWithParent(getModel(), field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSelfId(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        getModel().setId(generatedKeys.getInt(1));
    }
}
