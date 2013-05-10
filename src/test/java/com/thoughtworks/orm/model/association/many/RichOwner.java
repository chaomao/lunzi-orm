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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RichOwner)) return false;

        RichOwner richOwner = (RichOwner) o;

        if (houses != null ? !houses.equals(richOwner.houses) : richOwner.houses != null) return false;
        if (name != null ? !name.equals(richOwner.name) : richOwner.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (houses != null ? houses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RichOwner{" +
                "name='" + name + '\'' +
                ", houses=" + houses +
                '}';
    }
}
