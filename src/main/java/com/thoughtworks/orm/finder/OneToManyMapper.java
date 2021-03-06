package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasMany;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.orm.ModelHelper.getAssociationField;

class OneToManyMapper implements Mapper {
    private Field associatedField;

    public OneToManyMapper(Field associatedField) {
        this.associatedField = associatedField;
    }

    @Override
    public String getForeignKey() {
        return getAnnotation().foreignKey();
    }

    @Override
    public Class<?> getAssociationClass() {
        return getAnnotation().klass();
    }

    @Override
    public void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {
        getAssociationField(model, associatedField.getType()).set(model, entry.getValue());
    }

    private HasMany getAnnotation() {
        return this.associatedField.getAnnotation(HasMany.class);
    }
}
