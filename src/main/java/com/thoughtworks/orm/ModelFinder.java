package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.HasMany;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.ModelHelper.getTableName;
import static com.thoughtworks.orm.QueryGenerator.getFindByIdQuery;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;

public class ModelFinder {

    public static <T> T findById(Class<T> modelClass, int id) {
        try {
            String findByIdQuery = getFindByIdQuery(modelClass);
            ArrayList<Model> models = getObject(modelClass, findByIdQuery, id);
            return (T) models.get(0);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static <T> ArrayList<Model> getObject(Class<T> modelClass, String query, Object... params) throws SQLException {
        ResultSet resultSet = getResultSet(query, params);
        ArrayList<Model> objects = createObjectsFromResult(modelClass, resultSet);
        for (Model model : objects) {
            setChildren(model, model.getId());
        }
        return objects;
    }

    private static ResultSet getResultSet(String findByIdQuery, Object... params) throws SQLException {
        PreparedStatement statement = ConnectionManager.getDBConnection().prepareStatement(findByIdQuery);
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    private static void setChildren(Object model, int parentId) {
        Iterable<Field> associationFields = ModelHelper.getHasAssociationFields(model);
        for (Field field : associationFields) {
            if (field.isAnnotationPresent(HasOne.class)) {
                processHasOne(model, field, parentId);
            } else {
                processHasMany(model, field, parentId);
            }
        }
    }

    private static void processHasOne(Object model, Field field, int parentId) {
        HasOne annotation = field.getAnnotation(HasOne.class);
        String foreignKey = annotation.foreignKey();
        Class<?> childType = annotation.klass();
        try {
            ArrayList<Model> children = getChildren(childType, foreignKey, parentId);
            if (!children.isEmpty()) {
                field.set(model, children.get(0));
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void processHasMany(Object model, Field field, int parentId) {
        HasMany annotation = field.getAnnotation(HasMany.class);
        String foreignKey = annotation.foreignKey();
        Class childType = annotation.klass();
        try {
            ArrayList<Model> children = getChildren(childType, foreignKey, parentId);
            if (!children.isEmpty()) {
                field.set(model, children);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Model> getChildren(Class childType, String foreignKey, int parentId) throws SQLException, InstantiationException, IllegalAccessException {
        String criteria = String.format("%s = ?", foreignKey);
        String whereQuery = getWhereQuery(getTableName(childType), criteria);
        return getObject(childType, whereQuery, parentId);
    }

    private static ArrayList<Model> createObjectsFromResult(Class modelClass, ResultSet resultSet) throws SQLException {
        ArrayList<Model> resultLists = new ArrayList<>();
        while (resultSet.next()) {
            try {
                Model child = (Model) modelClass.newInstance();
                Iterable<Field> columns = getAttributesForInsertWithId(child);
                for (Field input : columns) {
                    String columnName = getColumnName(input);
                    Class<?> columnType = input.getType();
                    Object value = generateAttribute(resultSet, columnName, columnType);
                    input.set(child, value);
                }
                resultLists.add(child);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return resultLists;
    }

    private static Object generateAttribute(ResultSet resultSet, String columnName, Class<?> columnType) throws SQLException {
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

    private static String getColumnName(Field input) {
        return input.getName();
    }
}
