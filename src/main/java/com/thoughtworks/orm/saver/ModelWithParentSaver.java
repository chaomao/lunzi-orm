package com.thoughtworks.orm.saver;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;
import com.thoughtworks.orm.QueryGenerator;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ModelWithParentSaver extends Saver {

    private Model parent;
    private Field associatedField;

    public ModelWithParentSaver(Model model, Model parent, Field associatedField) {
        super(model);
        this.parent = parent;
        this.associatedField = associatedField;
    }

    @Override
    public void save() {
        PreparedStatement statement;
        try {
            List<Field> attributes = Lists.newArrayList(ModelHelper.getAttributesForInsert(getModel()));
            attributes.add(associatedField);
            String query = QueryGenerator.insertQuery(getModel(), attributes);
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

    private Object createAttributeValue(Field field) throws IllegalAccessException {
        Object value = field.get(getModel());
        return field.getType().isEnum() ? value.toString() : value;
    }

    private void updateSelfId(PreparedStatement statement) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();
        generatedKeys.next();
        getModel().setId(generatedKeys.getInt(1));
    }
}
