package com.thoughtworks.orm.model.association.one;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

public class Owner extends Model {

    private String name;
    @HasOne(foreignKey = "owner_id", klass = House.class)
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

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", house=" + house +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Owner)) return false;

        Owner owner = (Owner) o;

        if (house != null ? !house.equals(owner.house) : owner.house != null) return false;
        if (name != null ? !name.equals(owner.name) : owner.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (house != null ? house.hashCode() : 0);
        return result;
    }
}
