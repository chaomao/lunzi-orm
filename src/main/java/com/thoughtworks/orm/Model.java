package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.Column;

public abstract class Model {
    @Column
    protected int id;
}
