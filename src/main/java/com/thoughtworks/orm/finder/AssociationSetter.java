package com.thoughtworks.orm.finder;

import java.lang.reflect.Field;

public interface AssociationSetter {
    void process(Object model, Field field, int parentId);
}
