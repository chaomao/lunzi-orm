package com.thoughtworks.orm.model.association.one.to.one;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

public class Owner extends Model {

    private String name;
    @HasOne(foreignKey="owner_id")
    private House house;

    public Owner() {
    }

    public Owner(String name, House house) {
        this.name = name;
        this.house = house;
    }

    public House getHouse() {
        return house;
    }
}
