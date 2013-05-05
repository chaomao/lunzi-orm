package com.thoughtworks.orm;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.thoughtworks.orm.annotation.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Iterables.filter;

class ModelHelper {

    public static String getTableName(Object object) {
        return object.getClass().getSimpleName().toLowerCase();
    }

    public static Iterable<Field> getAttributesWithId(Object author) {
        try {
            ArrayList<Field> fields = Lists.newArrayList(author.getClass().getDeclaredFields());
            fields.add(author.getClass().getSuperclass().getDeclaredField("id"));
            return filter(fields, new Predicate<Field>() {
                @Override
                public boolean apply(Field input) {
                    return input.isAnnotationPresent(Column.class);
                }
            });
        } catch (NoSuchFieldException e) {
            throw new RuntimeException();
        }
    }

    public static Iterable<Field> getAttributes(Object author) {
        return filter(Lists.newArrayList(author.getClass().getDeclaredFields()), new Predicate<Field>() {
            @Override
            public boolean apply(Field input) {
                return input.isAnnotationPresent(Column.class);
            }
        });
    }

}
