package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.orm.ModelHelper.getAssociationField;

class OneToOneMapper implements Mapper {
    private Field associationField;

    public OneToOneMapper(Field associationField) {
        this.associationField = associationField;
    }

    @Override
    public String getForeignKey() {
        return this.associationField.getAnnotation(HasOne.class).foreignKey();
    }

    @Override
    public Class<?> getAssociationClass() {
        return this.associationField.getAnnotation(HasOne.class).klass();
    }

    @Override
    public void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {
        Field targetField = getAssociationField(model, associationField.getType());
        targetField.set(model, entry.getValue().get(0));
    }
}
