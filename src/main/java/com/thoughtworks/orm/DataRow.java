package com.thoughtworks.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataRow {
    private Map<String, Object> map = new HashMap<>();

    public Object generateFieldValue(Field field) throws SQLException {
        String columnName = field.getName();
        Class<?> columnType = field.getType();
        if (columnType.isEnum()) {
            return this.getEnumValue(columnName, columnType);
        } else if (columnType.equals(ArrayList.class)) {
            return this.getArrayListValue(columnName, columnType);
        }
        return getObject(columnName);
    }

    public void put(String columnName, Object object) {
        map.put(columnName, object);
    }

    public Object getObject(String columnName) {
        return map.get(columnName);
    }

    public String getString(String columnName) {
        return String.valueOf(map.get(columnName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataRow)) return false;

        DataRow dataRow = (DataRow) o;

        if (!map.equals(dataRow.map)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    private Object getEnumValue(String columnName, Class columnType) throws SQLException {
        String object = getString(columnName);
        return Enum.valueOf((Class<Enum>) columnType, object);
    }

    //todo change serialize way
    private Object getArrayListValue(String columnName, Class<?> columnType) {
        try {
            String object = getString(columnName);
            String[] splits = object.split("--");
            ArrayList result = (ArrayList) columnType.getConstructor().newInstance();
            Collections.addAll(result, splits);
            return result;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
