package com.thoughtworks.orm.finder;

import com.google.common.base.Predicate;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.tryFind;
import static com.thoughtworks.orm.ModelHelper.getAssociationField;

class OneToOneSetter extends AssociationSetter {

    @Override
    public void process(List<Model> parents, Field associationField) {
        //todo move annotation to instance variable
        HasOne annotation = associationField.getAnnotation(HasOne.class);
        String foreignKey = annotation.foreignKey();
        Class<?> childType = annotation.klass();
        try {
            Iterable<Object> parentsIds = getParentIds(parents);
            Map<Integer, List<Model>> children = getChildrenWithEagerLoading(childType, foreignKey, toArray(parentsIds, Object.class));
            for (final Map.Entry<Integer, List<Model>> entry : children.entrySet()) {
                Model model = tryFind(parents, new Predicate<Model>() {
                    @Override
                    public boolean apply(Model input) {
                        return entry.getKey().equals(input.getId());
                    }
                }).orNull();
                if (model != null) {
                    Field targetField = getAssociationField(model, associationField.getType());
                    targetField.set(model, entry.getValue().get(0));
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
