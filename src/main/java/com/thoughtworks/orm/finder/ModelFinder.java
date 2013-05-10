package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.thoughtworks.orm.ConnectionManager.getResultSet;
import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.ModelHelper.getHasAssociationFields;
import static com.thoughtworks.orm.QueryGenerator.getFindAllQuery;
import static com.thoughtworks.orm.QueryGenerator.getFindByIdQuery;

public class ModelFinder {

    public static <T> T findById(Class<T> modelClass, int id) {
        String findByIdQuery = getFindByIdQuery(modelClass);
        ArrayList<Model> models = getModels(modelClass, findByIdQuery, id);
        return (T) models.get(0);
    }

    public static <T> List<T> findAll(Class<T> modelClass) {
        return (List<T>) getModels(modelClass, getFindAllQuery(modelClass));
    }

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
                AssociationSetter associationSetter = field.isAnnotationPresent(HasOne.class) ?
                        new OneToOneSetter(models, field) :
                        new OneToManySetter(models, field);
                associationSetter.process();
            }
        }
    }

    static ArrayList<Model> createObjectsFromResult(Class modelClass, ResultSet resultSet) {
        ArrayList<Model> resultLists = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Model child = createModelWithoutAssociation(modelClass, resultSet);
                resultLists.add(child);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return resultLists;
    }

    static Model createModelWithoutAssociation(Class modelClass, ResultSet resultSet) throws InstantiationException, IllegalAccessException, SQLException {
        Model child = (Model) modelClass.newInstance();
        for (Field field : getAttributesForInsertWithId(child)) {
            Object value = generateFieldValue(resultSet, field);
            field.set(child, value);
        }
        return child;
    }

    static Object generateFieldValue(ResultSet resultSet, Field field) throws SQLException {
        String columnName = field.getName();
        Class<?> columnType = field.getType();
        if (columnType.isEnum()) {
            return getEnumValue(resultSet, columnName, columnType);
        } else if (columnType.equals(ArrayList.class)) {
            return getArrayListValue(resultSet, columnName, columnType);
        }
        return resultSet.getObject(columnName, columnType);
    }

    private static Object getArrayListValue(ResultSet resultSet, String columnName, Class<?> columnType) {
        try {
            String object = resultSet.getObject(columnName, String.class);
            String[] splits = object.split("--");
            ArrayList result = (ArrayList) columnType.getConstructor().newInstance();
            Collections.addAll(result, splits);
            return result;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static Object getEnumValue(ResultSet resultSet, String columnName, Class columnType) throws SQLException {
        String object = resultSet.getObject(columnName, String.class);
        return Enum.valueOf((Class<Enum>) columnType, object);
    }
}
