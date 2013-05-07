package com.thoughtworks.orm.saver;

import com.thoughtworks.orm.Model;

public abstract class Saver {
    protected Model model;

    public Saver(Model model) {
        this.model = model;
    }

    public abstract void save();

    public Model getModel() {
        return model;
    }
}
