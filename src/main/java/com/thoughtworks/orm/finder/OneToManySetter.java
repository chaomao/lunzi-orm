package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class OneToManySetter extends AssociationSetter {

    private Mapper many;

    public OneToManySetter(List<Model> parents, Field associationField) {
        super(parents, associationField);
        many = new OneToMany(associationField);
        setForeignKey(many.getForeignKey());
        setChildType(many.getChildType());
    }

    @Override
    protected void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {
        many.mapChildToParent(entry, model);
    }
}

