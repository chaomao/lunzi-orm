package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.HasMany;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.ModelHelper.getTableName;
import static com.thoughtworks.orm.QueryGenerator.getFindByIdQuery;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;

public class ModelFinder {

    public static <T> T findById(Class<T> modelClass, int id) {
        try {
            Object object = modelClass.getConstructor().newInstance();
            PreparedStatement statement = ConnectionManager.getDBConnection().prepareStatement(getFindByIdQuery(modelClass));
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            Iterable<Field> columns = getAttributesForInsertWithId(object);
            Object model = setObject(object, resultSet, columns);
            setChildren(model, id);
            return (T) model;
        } catch (SQLException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
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
            String criteria = String.format("%s = ?", foreignKey);
            String whereQuery = getWhereQuery(getTableName(childType), criteria);
            PreparedStatement statement = ConnectionManager.getDBConnection().prepareStatement(whereQuery);
            statement.setObject(1, parent_id);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Object> child1 = createChild(childType, resultSet);
            field.set(model, child1.get(0));
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void processHasMany(Object model, Field field, int parent_id) {
        HasMany annotation = field.getAnnotation(HasMany.class);
        String foreignKey = annotation.foreignKey();
        Class childType = annotation.klass();
        try {
            String criteria = String.format("%s = ?", foreignKey);
            String whereQuery = getWhereQuery(getTableName(childType), criteria);
            PreparedStatement statement = ConnectionManager.getDBConnection().prepareStatement(whereQuery);
            statement.setObject(1, parent_id);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Object> resultLists = createChild(childType, resultSet);
            field.set(model, resultLists);
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Object> createChild(Class childType, ResultSet resultSet) throws SQLException, InstantiationException, IllegalAccessException {
        ArrayList<Object> resultLists = new ArrayList<>();
        while (resultSet.next()) {
            Object child = childType.newInstance();
            Iterable<Field> columns = getAttributesForInsertWithId(child);

            for (Field input : columns) {
                String columnName = getColumnName(input);
                Class<?> columnType = input.getType();
                try {
                    Object value = generateAttribute(resultSet, columnName, columnType);
                    input.setAccessible(true);
                    input.set(child, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            resultLists.add(child);
        }
        return resultLists;
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
            for (String split : splits) {
                result.add(split);
            }
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
