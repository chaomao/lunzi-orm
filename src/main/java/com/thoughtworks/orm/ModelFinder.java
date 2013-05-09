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
            ResultSet resultSet = getResultSet(id, findByIdQuery);
            ArrayList<Object> objects = createObjectsFromResult(modelClass, resultSet);
            Object model = objects.get(0);
            setChildren(model, id);
            return (T) model;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static ResultSet getResultSet(int id, String findByIdQuery) throws SQLException {
        PreparedStatement statement = ConnectionManager.getDBConnection().prepareStatement(findByIdQuery);
        statement.setObject(1, id);
        return statement.executeQuery();
    }

    private static void setChildren(Object model, int id) {
        Iterable<Field> associationFields = ModelHelper.getHasAssociationFields(model);
        for (Field field : associationFields) {
            if (field.isAnnotationPresent(HasOne.class)) {
                processHasOne(model, field, id);
            } else {
                processHasMany(model, field, id);
            }
        }
    }

    private static void processHasOne(Object model, Field field, int parent_id) {
        String foreignKey = field.getAnnotation(HasOne.class).foreignKey();
        Class<?> childType = field.getType();
        try {
            ArrayList<Object> children = getChildren(parent_id, foreignKey, childType);
            if (!children.isEmpty()) {
                field.set(model, children.get(0));
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void processHasMany(Object model, Field field, int parent_id) {
        HasMany annotation = field.getAnnotation(HasMany.class);
        String foreignKey = annotation.foreignKey();
        Class childType = annotation.klass();
        try {
            ArrayList<Object> children = getChildren(parent_id, foreignKey, childType);
            if (!children.isEmpty()) {
                field.set(model, children);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Object> getChildren(int parent_id, String foreignKey, Class childType) throws SQLException, InstantiationException, IllegalAccessException {
        String criteria = String.format("%s = ?", foreignKey);
        String whereQuery = getWhereQuery(getTableName(childType), criteria);
        ResultSet resultSet = getResultSet(parent_id, whereQuery);
        return createObjectsFromResult(childType, resultSet);
    }

    private static ArrayList<Object> createObjectsFromResult(Class modelClass, ResultSet resultSet) throws SQLException {
        ArrayList<Object> resultLists = new ArrayList<>();
        while (resultSet.next()) {
            try {
                Object child = modelClass.newInstance();
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
