package com.thoughtworks.orm.model.association.one;

import com.thoughtworks.orm.Model;

public class House extends Model {
    private int size;

    public House() {
    }

    public House(int size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof House)) return false;

        House house = (House) o;

        if (size != house.size) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return size;
    }
}
