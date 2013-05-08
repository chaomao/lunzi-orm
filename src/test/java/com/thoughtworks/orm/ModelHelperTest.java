package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.model.association.many.RichOwner;
import com.thoughtworks.orm.model.association.one.Owner;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ModelHelperTest {

    @Test
    public void should_return_has_one_association_fields() throws NoSuchFieldException {
        Iterable<Field> hasOneAssociationFields = ModelHelper.getHasAssociationFields(new Owner());
        Field house = Owner.class.getDeclaredField("house");

        ArrayList<Field> actual = Lists.newArrayList(hasOneAssociationFields);

        assertThat(actual.size(), is(1));
        assertThat(actual, hasItems(house));
    }

    @Test
    public void should_not_return_has_many_field() throws NoSuchFieldException {
        Iterable<Field> fields = ModelHelper.getAttributesForInsert(new RichOwner());
        Field houseField = RichOwner.class.getDeclaredField("houses");
        ArrayList<Field> actual = Lists.newArrayList(fields);

        assertThat(actual.size(), is(1));
        assertThat(actual, not(hasItem(houseField)));


    }
}
