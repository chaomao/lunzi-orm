package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;
import com.thoughtworks.orm.QueryGenerator;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.thoughtworks.orm.ModelHelper.getHasAssociationFields;
import static com.thoughtworks.orm.QueryGenerator.getFindByIdQuery;

public class ModelFinder {

    public static <T> T findById(Class<T> modelClass, int id) {
        String findByIdQuery = getFindByIdQuery(modelClass);
        ArrayList<Model> models = getModels(modelClass, findByIdQuery, id);
        return (T) models.get(0);
    }

    static <T> ArrayList<Model> getModels(Class<T> modelClass, String query, Object... params) {
        if (params == null) {
            params = new Object[]{};
        }
        ArrayList<Model> models = createObjectsFromResult(modelClass, getResultSet(query, params));
        setChildren(modelClass, models);
        return models;
    }

    private static ResultSet getResultSet(String findByIdQuery, Object... params) {
        PreparedStatement statement;
        try {
            statement = ConnectionManager.getDBConnection().prepareStatement(findByIdQuery);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static void setChildren(Class modelClass, ArrayList<Model> models) {
        if (!models.isEmpty()) {
            for (Field field : getHasAssociationFields(modelClass)) {
                getAssociationSetter(field).process(models, field);
            }
        }
    }

    private static AssociationSetter getAssociationSetter(Field field) {
        return field.isAnnotationPresent(HasOne.class) ?
                new OneToOneSetter() :
                new OneToManySetter();
    }

    private static ArrayList<Model> createObjectsFromResult(Class modelClass, ResultSet resultSet) {
        ArrayList<Model> resultLists = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Model child = (Model) modelClass.newInstance();
                for (Field input : ModelHelper.getAttributesForInsertWithId(child)) {
                    Object value = generateAttribute(resultSet, input);
                    input.set(child, value);
                }
                resultLists.add(child);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return resultLists;
    }

    private static Object generateAttribute(ResultSet resultSet, Field input) throws SQLException {
        String columnName = input.getName();
        Class<?> columnType = input.getType();
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

    public static <T> List<T> findAll(Class<T> modelClass) {
        return (List<T>) getModels(modelClass, QueryGenerator.getFindAllQuery(modelClass));
    }
}
