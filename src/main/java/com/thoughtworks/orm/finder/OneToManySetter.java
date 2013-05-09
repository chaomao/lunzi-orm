package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasMany;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

class OneToManySetter extends AssociationSetter {

    @Override
    public void process(Object model, Field field, int parentId) {
        HasMany annotation = field.getAnnotation(HasMany.class);
        String foreignKey = annotation.foreignKey();
        Class childType = annotation.klass();
        try {
            ArrayList<Model> children = getChildren(childType, foreignKey, parentId);
            if (!children.isEmpty()) {
                field.set(model, children);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
