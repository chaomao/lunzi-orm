package com.thoughtworks.orm;

import com.thoughtworks.orm.model.association.one.House;
import com.thoughtworks.orm.model.association.one.Owner;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class OneToOneAssociationTest extends DBTest {

    @Test
    public void should_save_owner_and_house_at_one_time() {
        House house = new House(127);
        Owner owner = new Owner("Mao Chao", house);
        owner.save();

        assertThat(owner.getId(), not(is(0)));
        assertThat(owner.getHouse().getId(), is(house.getId()));
    }
}
