package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class OneToOneSetter extends AssociationSetter {

    private Mapper mapper;

    public OneToOneSetter(List<Model> parents, Field associationField) {
        super(parents, associationField);
        mapper = new OneToOne(associationField);
        setForeignKey(mapper.getForeignKey());
        setChildType(mapper.getChildType());
    }

    @Override
    protected void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {
        mapper.mapChildToParent(entry, model);
    }
}
