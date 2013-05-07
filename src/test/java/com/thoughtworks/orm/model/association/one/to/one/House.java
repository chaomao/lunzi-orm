package com.thoughtworks.orm.model.association.one.to.one;

import com.thoughtworks.orm.Model;

public class House extends Model {
    private int size;

    public House() {
    }

    public House(int size) {
        this.size = size;
    }
}
