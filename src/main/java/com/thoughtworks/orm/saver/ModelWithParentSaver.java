package com.thoughtworks.orm.saver;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ModelWithParentSaver extends ModelSaver {

    private Model parent;
    private Field associatedField;

    public ModelWithParentSaver(Model model, Model parent, Field associatedField) {
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
        return ModelHelper.hasAssociation(field) ? Integer.valueOf(parent.getId()) :
                super.createAttributeValue(field);
    }
}
