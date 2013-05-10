package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.association.many.RichOwner;
import com.thoughtworks.orm.model.association.many.Room;
import com.thoughtworks.orm.model.association.one.House;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void should_eager_load_all_houses_and_all_rooms_when_get_all_rich_owners() {
        List<RichOwner> owners = Lists.newArrayList(
                new RichOwner("Mao", Lists.newArrayList(new House(100, new Room(1)), new House(110, new Room(3)))),
                new RichOwner("Er", null),
                new RichOwner("Chao", Lists.newArrayList(new House(300, new Room(3)), new House(310, new Room(2))))

        );
        for (RichOwner owner : owners) {
            owner.save();
        }

        ConnectionManager.connectNumber = 0;
        List<RichOwner> result = ModelFinder.findAll(RichOwner.class);

        assertThat(result, is(owners));
        assertThat(ConnectionManager.connectNumber, is(3));
    }
}
