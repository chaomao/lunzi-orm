package com.thoughtworks.orm.model.association.many;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasMany;
import com.thoughtworks.orm.model.association.one.House;

import java.util.ArrayList;

public class RichOwner extends Model {

    private String name;
    @HasMany(foreignKey = "owner_id", klass = House.class)
    private ArrayList<House> houses;

    public RichOwner() {
    }

    public RichOwner(String name, ArrayList<House> houses) {
        this.name = name;
        this.houses = houses;
    }

    public ArrayList<House> getHouses() {
        return houses;
    }
}
