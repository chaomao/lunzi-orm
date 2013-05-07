package com.thoughtworks.orm;

import com.thoughtworks.orm.model.Author;
import com.thoughtworks.orm.model.association.one.to.one.House;
import com.thoughtworks.orm.model.association.one.to.one.Owner;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryGeneratorTest {

    @Test
    //todo rename it
    public void should_generate_insert_query() {
        final Author object = new Author();
        String actual = QueryGenerator.insertQuery(object, ModelHelper.getAttributesForInsert(object));

        String expect = "INSERT INTO author (name) VALUES (?)";

        assertThat(actual, is(expect));
    }

    @Test
    public void should_generate_insert_query_without_has_one_association() {
        final Owner object = new Owner();
        String actual = QueryGenerator.insertQuery(object, ModelHelper.getAttributesForInsert(object));

        String expect = "INSERT INTO owner (name) VALUES (?)";

        assertThat(actual, is(expect));
    }
}
