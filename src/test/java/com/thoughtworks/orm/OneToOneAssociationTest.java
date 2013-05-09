package com.thoughtworks.orm;

import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.association.one.House;
import com.thoughtworks.orm.model.association.one.Owner;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class OneToOneAssociationTest extends DBTest {

    @Test
    public void should_save_owner_and_house_at_one_time() {
        House house = new House(127);
        Owner owner = new Owner("Mao Chao", house);
        owner.save();

        Owner result = ModelFinder.findById(Owner.class, owner.getId());
        assertThat(result.getHouse(), is(house));
    }

    @Test
    public void should_save_owner_without_house() {
        Owner owner = new Owner("Mao Chao", null);
        owner.save();

        Owner result = ModelFinder.findById(Owner.class, owner.getId());
        assertThat(result.getHouse(), nullValue());
    }
}
