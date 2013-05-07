package com.thoughtworks.orm;

import com.google.common.base.Function;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;

import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.orm.ModelHelper.getTableName;

public class QueryGenerator {
    private static final String INSERT_QUERY = "INSERT INTO %s (%s) VALUES (%s)";

    public static String insertQuery(Object object, Iterable<Field> attributesForInsert) {
        return String.format(INSERT_QUERY, getTableName(object),
                join(getAttributeNames(attributesForInsert), ", "),
                join(getAttributePlaceHolders(attributesForInsert), ", "));
    }

    private static Iterable<String> getAttributePlaceHolders(Iterable attributes) {
        return transform(attributes, new Function<Object, String>() {
            @Override
            public String apply(Object input) {
                return "?";
            }
        });
    }

    private static Iterable<String> getAttributeNames(Iterable<Field> attributes) {
        return transform(attributes, new Function<Field, String>() {
            @Override
            public String apply(Field input) {
                return input.isAnnotationPresent(HasOne.class) ?
                        input.getAnnotation(HasOne.class).foreignKey() :
                        input.getName();
            }
        });
    }

    public static String join(Iterable collection, String delimiter) {
        StringBuffer temp = new StringBuffer();
        for (Object o : collection) {
            temp.append(o);
            temp.append(delimiter);
        }
        String result = temp.toString();
        return result.substring(0, result.length() - delimiter.length());
    }
}
