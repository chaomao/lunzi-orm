package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.thoughtworks.orm.finder.ModelFinder.getChildren;

class OneToOneSetter implements AssociationSetter {

    @Override
    public void process(Object model, Field field, int parentId) {
        HasOne annotation = field.getAnnotation(HasOne.class);
        String foreignKey = annotation.foreignKey();
        Class<?> childType = annotation.klass();
        try {
            ArrayList<Model> children = getChildren(childType, foreignKey, parentId);
            if (!children.isEmpty()) {
                field.set(model, children.get(0));
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
