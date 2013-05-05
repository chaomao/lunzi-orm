package com.thoughtworks.orm;

import com.google.common.base.Function;

import java.lang.reflect.Field;

import static com.google.common.collect.Iterables.transform;

public class QueryGenerator {
    public static String insert(Object object) {
        Iterable<Field> attributes = ModelHelper.getAttributes(object);
        Iterable<String> attributeNames = transform(attributes, new Function<Field, String>() {
            @Override
            public String apply(Field input) {
                return input.getName();
            }
        });

        Iterable<String> attributePlaceHolder = transform(attributes, new Function<Field, String>() {
            @Override
            public String apply(Field input) {
                return "?";
            }
        });

        return "INSERT INTO " + ModelHelper.getTableName(object) + " (" +
                join(attributeNames, ", ") + ")" + " VALUES (" + join(attributePlaceHolder, ", ")
                + ")";
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
