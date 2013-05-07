package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.model.association.one.to.one.Owner;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ModelHelperTest {

    @Test
    public void should_return_has_one_association_fields() {
        Iterable<Field> hasOneAssociationFields = ModelHelper.getHasOneAssociationFields(new Owner());
        try {
            Field house = Owner.class.getDeclaredField("house");
            ArrayList<Field> actual = Lists.newArrayList(hasOneAssociationFields);
            assertThat(actual.size(), is(1));
            assertThat(actual, hasItems(house));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            fail();
        }
    }
}
