package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.orm.ConnectionManager.getResultSet;
import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.ModelHelper.getHasAssociationFields;
import static com.thoughtworks.orm.finder.ColumnValueGenerator.generateFieldValue;

//TODO I should get all data from ResultSet, not pass ResultSet everywhere
class ModelFactory {

    static <T> ArrayList<Model> getModels(Class<T> modelClass, String query, Object... params) {
        if (params == null) {
            params = new Object[]{};
        }
        ArrayList<Model> models = createObjectsFromResult(modelClass, getResultSet(query, params));
        setChildren(modelClass, models);
        return models;
    }

    static void setChildren(Class modelClass, List<Model> models) {
        if (!models.isEmpty()) {
            for (Field field : getHasAssociationFields(modelClass)) {
                Mapper mapper = field.isAnnotationPresent(HasOne.class) ?
                        new OneToOneMapper(field) :
                        new OneToManyMapper(field);
                AssociationSetter associationSetter = new AssociationSetter(models, mapper);
                associationSetter.process();
            }
        }
    }

    static Model createModelWithoutAssociation(Class modelClass, ResultSet resultSet) throws InstantiationException, IllegalAccessException, SQLException {
        Model child = (Model) modelClass.newInstance();
        for (Field field : getAttributesForInsertWithId(child)) {
            field.set(child, generateFieldValue(resultSet, field));
        }
        return child;
    }

    private static ArrayList<Model> createObjectsFromResult(Class modelClass, ResultSet resultSet) {
        try {
            ArrayList<Model> resultLists = new ArrayList<>();
            while (resultSet.next()) {
                resultLists.add(createModelWithoutAssociation(modelClass, resultSet));
            }
            return resultLists;
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
