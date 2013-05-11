package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.model.Author;
import com.thoughtworks.orm.model.association.many.RichOwner;
import com.thoughtworks.orm.model.association.one.House;
import com.thoughtworks.orm.model.association.one.Owner;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryGeneratorTest {

    @Test
    public void should_generate_insert_query_without_association() {
        final Author object = new Author();
        String actual = QueryGenerator.insertQuery(object, ModelHelper.getAttributesWithoutAssociation(object));

        String expect = "INSERT INTO author (name) VALUES (?)";

        assertThat(actual, is(expect));
    }

    @Test
    public void should_generate_insert_query_without_has_one_association() {
        final Owner object = new Owner();
        String actual = QueryGenerator.insertQuery(object, ModelHelper.getAttributesWithoutAssociation(object));

        String expect = "INSERT INTO owner (name) VALUES (?)";

        assertThat(actual, is(expect));
    }

    @Test
    public void should_generate_insert_query_with_has_many_association() throws NoSuchFieldException {
        final House house = new House();
        ArrayList<Field> fields = Lists.newArrayList(ModelHelper.getAttributesWithoutAssociation(house));
        Field housesField = RichOwner.class.getDeclaredField("houses");
        housesField.setAccessible(true);
        fields.add(housesField);
        String actual = QueryGenerator.insertQuery(house, fields);

        String expect = "INSERT INTO house (size, owner_id) VALUES (?, ?)";

        assertThat(actual, is(expect));
    }
}
