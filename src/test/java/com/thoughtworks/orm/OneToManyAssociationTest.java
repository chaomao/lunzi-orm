package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.association.many.RichOwner;
import com.thoughtworks.orm.model.association.one.House;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class OneToManyAssociationTest extends DBTest {

    @Test
    public void should_save_owner_and_house_at_one_time() {
        ArrayList<House> houses = Lists.newArrayList(new House(127), new House(128));
        RichOwner owner = new RichOwner("Mao Chao", houses);
        owner.save();

        RichOwner result = ModelFinder.findById(RichOwner.class, owner.getId());
        assertThat(result.getHouses(), is(houses));
    }

    @Test
    public void should_save_rich_owner_without_houses() {
        RichOwner owner = new RichOwner("Mao Chao", null);
        owner.save();

        RichOwner result = ModelFinder.findById(RichOwner.class, owner.getId());
        assertThat(result.getHouses(), nullValue());
    }
}
