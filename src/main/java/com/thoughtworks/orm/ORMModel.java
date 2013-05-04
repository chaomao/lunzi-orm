package com.thoughtworks.orm;

import com.thoughtworks.orm.annotation.Column;

public abstract class ORMModel {
    @Column
    protected int id;
}
