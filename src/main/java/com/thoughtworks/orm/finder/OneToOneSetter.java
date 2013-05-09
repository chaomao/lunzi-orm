package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

class OneToOneSetter extends AssociationSetter {

    @Override
    public void process(List<Model> parents, Field associationField) {
        HasOne annotation = associationField.getAnnotation(HasOne.class);
        String foreignKey = annotation.foreignKey();
        Class<?> childType = annotation.klass();
        try {
            Iterable<Object> parentsIds = getParentIds(parents);
            ArrayList<Model> children = getChildrenWithEagerLoading(childType, foreignKey, toArray(parentsIds, Object.class));
            setEachChildToParent(parents, children, associationField);
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setEachChildToParent(List<Model> parents, ArrayList<Model> children, Field associationField) throws IllegalAccessException {
        for (int i = 0; i < children.size(); i++) {
            Model parent = parents.get(i);
            Field targetField = ModelHelper.getAssociationField(parent, associationField.getType());
            targetField.set(parent, children.get(i));
        }
    }

    private Iterable<Object> getParentIds(List<Model> parents) {
        return transform(parents, new Function<Model, Object>() {
            @Override
            public Object apply(Model input) {
                return input.getId();
            }
        });
    }
}
