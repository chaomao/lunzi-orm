package com.thoughtworks.orm;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ModelHelper {

    public static String getTableName(Object object) {
        return object.getClass().getSimpleName().toLowerCase();
    }

    public static Iterable<Field> getAttributesWithId(Object object) {
        ArrayList<Field> fields = Lists.newArrayList(getAttributes(object));
        fields.add(getIdField(object));
        return fields;
    }

    private static Field getIdField(Object object) {
        try {
            return object.getClass().getSuperclass().getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static Iterable<Field> getAttributes(Object object) {
        return Lists.newArrayList(object.getClass().getDeclaredFields());
    }
}
