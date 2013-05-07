package com.thoughtworks.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModelFinder {

    public static <T> T findById(Class<? extends Model> modelClass, int id) {
        try {
            Object object = modelClass.getConstructor().newInstance();
            String findByIDQuery = String.format("SELECT * FROM %s where id=%d", ModelHelper.getTableName(object), id);
            ResultSet resultSet = ConnectionManager.getDBConnection().createStatement().executeQuery(findByIDQuery);
            Iterable<Field> annotatedColumns = ModelHelper.getAttributesForInsertWithId(object);
            return (T) setObject(object, resultSet, annotatedColumns);
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static Object setObject(Object object, ResultSet resultSet, Iterable<Field> annotatedColumns) throws SQLException {
        resultSet.next();
        for (Field input : annotatedColumns) {
            String columnName = getColumnName(input);
            Class<?> columnType = input.getType();
            try {
                Object value = generateAttribute(resultSet, columnName, columnType);
                input.setAccessible(true);
                input.set(object, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    private static Object generateAttribute(ResultSet resultSet, String columnName, Class<?> columnType) throws SQLException {
        if (columnType.isEnum()) {
            String object = resultSet.getObject(columnName, String.class);
            return Enum.valueOf((Class<Enum>) columnType, object);
        }
        return resultSet.getObject(columnName, columnType);
    }

    private static String getColumnName(Field input) {
        return input.getName();
    }
}
