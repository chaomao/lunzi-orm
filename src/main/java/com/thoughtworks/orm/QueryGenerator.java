package com.thoughtworks.orm;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.orm.ModelHelper.*;

public class QueryGenerator {
    private static final String INSERT_QUERY = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String FIND_QUERY = "SELECT * FROM %s";
    private static final String FIND_BY_WHERE_QUERY = "SELECT * FROM %s WHERE %s";

    public static String insertQuery(Object object, Iterable<Field> attributesForInsert) {
        return String.format(INSERT_QUERY, getTableName(object),
                join(getAttributeNames(attributesForInsert), ", "),
                join(getAttributePlaceHolders(attributesForInsert), ", "));
    }

    public static Iterable<String> getAttributePlaceHolders(Iterable attributes) {
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
                return hasAssociation(input) ?
                        getForeignKey(input) :
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

    public static String getFindByIdQuery(Class klass) {
        return String.format(FIND_BY_WHERE_QUERY, getTableName(klass), "id=?");
    }

    public static String getFindAllQuery(Class klass) {
        return String.format(FIND_QUERY, getTableName(klass));
    }

    public static String getWhereQuery(Class childType, String foreignKey, Object... parentIds) {
        ArrayList<Object> objects = Lists.newArrayList(parentIds);
        String criteria = String.format("%s in (%s)", foreignKey, join(getAttributePlaceHolders(objects), ","));
        return String.format(FIND_BY_WHERE_QUERY, getTableName(childType), criteria);
    }
}
