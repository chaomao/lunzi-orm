package com.thoughtworks.orm;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.thoughtworks.orm.annotation.HasMany;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.google.common.collect.Iterables.filter;

public class ModelHelper {

    public static String getTableName(Object object) {
        return getTableName(object.getClass());
    }

    public static String getTableName(Class klass) {
        return klass.getSimpleName().toLowerCase();
    }

    public static Iterable<Field> getAttributesForInsertWithId(Object object) {
        ArrayList<Field> fields = Lists.newArrayList(getAttributesForInsert(object));
        fields.add(getIdField(object));
        return fields;
    }

    private static Field getIdField(Object object) {
        try {
            Field idField = object.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            return idField;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static Iterable<Field> getAttributesForInsert(Object object) {
        return filter(getAllAttributes(object), new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return !hasAssociation(input);
            }
        });
    }

    private static ArrayList<Field> getAllAttributes(Object object) {
        ArrayList<Field> fields = Lists.newArrayList(object.getClass().getDeclaredFields());
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return fields;
    }

    public static Iterable<Field> getHasAssociationFields(Object object) {
        return filter(getAllAttributes(object), new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return hasAssociation(input);
            }
        });
    }

    public static boolean hasAssociation(Field field) {
        return field.isAnnotationPresent(HasOne.class) ||
                field.isAnnotationPresent(HasMany.class);
    }

    public static String getForeignKey(Field field) {
        return field.isAnnotationPresent(HasOne.class) ?
                field.getAnnotation(HasOne.class).foreignKey() :
                field.getAnnotation(HasMany.class).foreignKey();
    }
}
