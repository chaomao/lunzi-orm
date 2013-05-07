package com.thoughtworks.orm;

import com.thoughtworks.orm.saver.ModelSaver;

public abstract class Model {
    protected int id;

    public void save() {
        new ModelSaver(this).save();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}