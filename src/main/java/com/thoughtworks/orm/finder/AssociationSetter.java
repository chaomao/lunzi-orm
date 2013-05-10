package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.thoughtworks.orm.Model;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.*;
import static com.thoughtworks.orm.ConnectionManager.getResultSet;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFinder.setChildren;

class AssociationSetter {

    protected final List<Model> parents;
    protected final Field associationField;
    private String foreignKey;
    private Class<?> childType;

    protected AssociationSetter(List<Model> parents, Field associationField) {
        this.parents = parents;
        this.associationField = associationField;
    }

    protected Map<Integer, List<Model>> getChildrenMap() throws SQLException, InstantiationException, IllegalAccessException {
        Object[] parentIds = getParentId();
        ResultSet resultSet = getResultSet(getWhereQuery(getChildType(), getForeignKey(), parentIds), parentIds);
        HashMap<Integer, List<Model>> resultMap = new HashMap<>();
        try {
            while (resultSet.next()) {
                Model child = ModelFinder.createModelWithoutAssociation(getChildType(), resultSet);
                Integer abc = (Integer) resultSet.getObject(getForeignKey());
                pushChildIntoMap(resultMap, child, abc);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        List<Model> children = new ArrayList<>();
        for (List<Model> models : resultMap.values()) {
            children.addAll(models);
        }
        setChildren(getChildType(), children);
        return resultMap;
    }

    private Object[] getParentId() {
        return toArray(transform(parents, new Function<Model, Object>() {
            @Override
            public Object apply(Model input) {
                return input.getId();
            }
        }), Object.class);
    }

    private void pushChildIntoMap(HashMap<Integer, List<Model>> resultLists, Model child, Integer abc) {
        if (!resultLists.containsKey(abc)) {
            resultLists.put(abc, new ArrayList<Model>());
        }
        resultLists.get(abc).add(child);
    }

    protected Model findModel(List<Model> parents, final Integer parentId) {
        return tryFind(parents, new Predicate<Model>() {
            @Override
            public boolean apply(Model input) {
                return parentId.equals(input.getId());
            }
        }).orNull();
    }

    protected void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException {

    }

    public void process() {
        try {
            Map<Integer, List<Model>> children = getChildrenMap();
            for (final Map.Entry<Integer, List<Model>> entry : children.entrySet()) {
                Model model = findModel(parents, entry.getKey());
                if (model != null) {
                    mapChildToParent(entry, model);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public Class<?> getChildType() {
        return childType;
    }

    public void setChildType(Class<?> childType) {
        this.childType = childType;
    }
}