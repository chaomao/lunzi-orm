package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasMany;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.orm.ModelHelper.getAssociationField;

class OneToMany implements Mapper {
    private Field associationField;

    public OneToMany(Field associationField) {
        this.associationField = associationField;
    }

    @Override
    public String getForeignKey() {
        return this.associationField.getAnnotation(HasMany.class).foreignKey();
    }

    @Override
    public Class<?> getChildType() {
        return this.associationField.getAnnotation(HasMany.class).klass();
    }

    @Override
    public void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {
        Field targetField = getAssociationField(model, associationField.getType());
        targetField.set(model, entry.getValue());
    }
}
