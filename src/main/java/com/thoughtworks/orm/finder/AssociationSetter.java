package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.thoughtworks.orm.Model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.thoughtworks.orm.ConnectionManager.getResultSet;
import static com.thoughtworks.orm.ModelHelper.getAttributesForInsertWithId;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFinder.generateFieldValue;
import static com.thoughtworks.orm.finder.ModelFinder.setChildren;

abstract class AssociationSetter {

    protected Map<Integer, List<Model>> getChildrenWithEagerLoading(Class childType, String foreignKey, Object... parentIds) throws SQLException, InstantiationException, IllegalAccessException {
        ResultSet resultSet = getResultSet(getWhereQuery(childType, foreignKey, parentIds), parentIds);
        HashMap<Integer, List<Model>> resultLists = new HashMap<>();
        try {
            while (resultSet.next()) {
                Model child = (Model) childType.newInstance();
                for (Field field : getAttributesForInsertWithId(child)) {
                    Object value = generateFieldValue(resultSet, field);
                    field.set(child, value);
                }
                Integer abc = (Integer) resultSet.getObject(foreignKey);
                if (!resultLists.containsKey(abc)) {
                    resultLists.put(abc, new ArrayList<Model>());
                }
                resultLists.get(abc).add(child);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        List<Model> children = new ArrayList<>();

        for (List<Model> models : resultLists.values()) {
            children.addAll(models);
        }
        setChildren(childType, children);
        return resultLists;
    }

    public abstract void process(List<Model> models, Field field);

    protected Iterable<Object> getParentIds(List<Model> parents) {
        return transform(parents, new Function<Model, Object>() {
            @Override
            public Object apply(Model input) {
                return input.getId();
            }
        });
    }
}
