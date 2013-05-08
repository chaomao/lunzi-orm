package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.model.association.many.RichOwner;
import com.thoughtworks.orm.model.association.one.House;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class OneToManyAssociationTest extends DBTest {

    @Test
    public void should_save_owner_and_house_at_one_time() {
        ArrayList<House> houses = Lists.newArrayList(new House(127), new House(128));
        RichOwner owner = new RichOwner("Mao Chao", houses);
        owner.save();

        assertThat(owner.getId(), not(is(0)));
        for (House house : owner.getHouses()) {
            assertThat(house.getId(), not(is(0)));
        }
    }
}
