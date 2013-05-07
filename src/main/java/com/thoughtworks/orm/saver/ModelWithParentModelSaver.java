package com.thoughtworks.orm.saver;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.util.List;

public class ModelWithParentModelSaver extends ModelSaver {

    private Model parent;
    private Field associatedField;

    public ModelWithParentModelSaver(Model model, Model parent, Field associatedField) {
        super(model);
        this.parent = parent;
        this.associatedField = associatedField;
    }

    @Override
    protected Iterable<Field> getAttributesForInsert() {
        List<Field> attributes = Lists.newArrayList(super.getAttributesForInsert());
        attributes.add(associatedField);
        return attributes;
    }

    @Override
    protected Object createAttributeValue(Field field) throws IllegalAccessException {
        return field.isAnnotationPresent(HasOne.class) ?
                Integer.valueOf(parent.getId()) :
                super.createAttributeValue(field);
    }
}
