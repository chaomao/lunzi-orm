package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.google.common.collect.Iterables.filter;

public class ModelFinder {

    public static <T> T findById(Class<? extends ORMModel> modelClass, int id)  {
        try {
            Object object = modelClass.getConstructor().newInstance();
            String findByIDQuery = String.format("SELECT * FROM %s where id=%d", ModelHelper.getTableName(object), id);
            ResultSet resultSet = ConnectionManager.getDBConnection().createStatement().executeQuery(findByIDQuery);
            Iterable<Field> annotatedColumns = ModelHelper.getAttributesWithId(object);
            return (T) setObject(object, resultSet, annotatedColumns);
        } catch (SQLException| InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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
                Object value = resultSet.getObject(columnName, columnType);
                input.setAccessible(true);
                input.set(object, value);
            } catch (IllegalAccessException ignored) {
            }
        }
        return object;
    }

    private static String getColumnName(Field input) {
        Column annotation = input.getAnnotation(Column.class);
        return annotation.value().isEmpty() ? input.getName() : annotation.value();
    }

}
