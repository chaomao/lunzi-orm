package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Field;
import java.util.List;

class OneToManySetter extends AssociationSetter {

//    @Override
//    public void process(Object model, Field field, int parentId) {
//        HasMany annotation = field.getAnnotation(HasMany.class);
//        String foreignKey = annotation.foreignKey();
//        Class childType = annotation.klass();
//        try {
//            ArrayList<Model> children = getChildrenWithEagerLoading(childType, foreignKey, parentId);
//            if (!children.isEmpty()) {
//                field.set(model, children);
//            }
//        } catch (SQLException | InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void process(List<Model> models, Field field) {
        throw new NotImplementedException();
    }
}
