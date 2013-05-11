package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.thoughtworks.orm.DataRow;
import com.thoughtworks.orm.DataSet;
import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.annotation.HasOne;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.orm.ConnectionManager.getDataSet;
import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.ModelHelper.getHasAssociationFields;

//TODO I should get all data from ResultSet, not pass ResultSet everywhere
class ModelFactory {

    static <T> ArrayList<Model> getModels(Class<T> modelClass, String query, Object... params) {
        if (params == null) {
            params = new Object[]{};
        }
        ArrayList<Model> models = createObjectsFromResult(modelClass, getDataSet(query, params));
        setChildren(modelClass, models);
        return models;
    }

    static void setChildren(Class modelClass, List<Model> models) {
        if (!models.isEmpty()) {
            for (Field field : getHasAssociationFields(modelClass)) {
                Mapper mapper = field.isAnnotationPresent(HasOne.class) ?
                        new OneToOneMapper(field) :
                        new OneToManyMapper(field);
                AssociationSetter associationSetter = new AssociationSetter(models, mapper);
                associationSetter.process();
            }
        }
    }

    static Model createModelWithoutAssociation(Class modelClass, DataRow dataRow) {
        try {
            Model child = (Model) modelClass.newInstance();
            for (Field field : getAttributesForInsertWithId(child)) {
                field.set(child, dataRow.generateFieldValue(field));
            }
            return child;
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static ArrayList<Model> createObjectsFromResult(final Class modelClass, DataSet dataSet) {
        return Lists.newArrayList(transform(dataSet, new Function<DataRow, Model>() {
            @Override
            public Model apply(DataRow row) {
                return createModelWithoutAssociation(modelClass, row);
            }
        }));
    }
}
