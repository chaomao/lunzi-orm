package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.association.many.Room;
import com.thoughtworks.orm.model.association.one.House;
import com.thoughtworks.orm.model.association.one.Owner;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void should_eager_load_all_houses_and_all_rooms_when_get_all_owners() {
        List<Owner> owners = Lists.newArrayList(
                new Owner("Mao", new House(100, new Room(1))),
                new Owner("Er", null),
                new Owner("Chao", new House(200, new Room(2)))
        );
        for (Owner owner : owners) {
            owner.save();
        }

        ConnectionManager.connectNumber = 0;
        List<Owner> result = ModelFinder.findAll(Owner.class);

        assertThat(result, is(owners));
        assertThat(ConnectionManager.connectNumber, is(3));
    }
}
