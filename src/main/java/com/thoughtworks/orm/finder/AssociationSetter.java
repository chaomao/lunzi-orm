package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.thoughtworks.orm.Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.*;
import static com.thoughtworks.orm.ConnectionManager.getResultSet;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFactory.createModelWithoutAssociation;
import static com.thoughtworks.orm.finder.ModelFactory.setChildren;

class AssociationSetter {

    private final List<Model> parents;
    private Mapper mapper;

    protected AssociationSetter(List<Model> parents, Mapper mapper) {
        this.parents = parents;
        this.mapper = mapper;
    }

    private Map<Integer, List<Model>> getChildrenMap() throws SQLException, InstantiationException, IllegalAccessException {
        Object[] parentIds = getParentId();
        ResultSet resultSet = getResultSet(getWhereQuery(mapper.getAssociationClass(), mapper.getForeignKey(), parentIds), parentIds);
        HashMap<Integer, List<Model>> resultMap = getResultMapFromResult(resultSet);
        updateChildrenAssociation(resultMap);
        return resultMap;
    }

    private HashMap<Integer, List<Model>> getResultMapFromResult(ResultSet resultSet) {
        HashMap<Integer, List<Model>> resultMap = new HashMap<>();
        try {
            while (resultSet.next()) {
                Model child = createModelWithoutAssociation(mapper.getAssociationClass(), resultSet);
                Integer parentId = (Integer) resultSet.getObject(mapper.getForeignKey());
                pushChildIntoMap(resultMap, child, parentId);
            }
        } catch (InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    private void updateChildrenAssociation(HashMap<Integer, List<Model>> resultMap) {
        List<Model> children = new ArrayList<>();
        for (List<Model> models : resultMap.values()) {
            children.addAll(models);
        }
        setChildren(mapper.getAssociationClass(), children);
    }

    private Object[] getParentId() {
        return toArray(transform(parents, new Function<Model, Object>() {
            @Override
            public Object apply(Model input) {
                return input.getId();
            }
        }), Object.class);
    }

    private void pushChildIntoMap(HashMap<Integer, List<Model>> resultLists, Model child, Integer parentId) {
        if (!resultLists.containsKey(parentId)) {
            resultLists.put(parentId, new ArrayList<Model>());
        }
        resultLists.get(parentId).add(child);
    }

    protected Model findModel(List<Model> parents, final Integer parentId) {
        return tryFind(parents, new Predicate<Model>() {
            @Override
            public boolean apply(Model input) {
                return parentId.equals(input.getId());
            }
        }).orNull();
    }

    public void process() {
        try {
            Map<Integer, List<Model>> children = getChildrenMap();
            for (final Map.Entry<Integer, List<Model>> entry : children.entrySet()) {
                Model model = findModel(parents, entry.getKey());
                if (model != null) {
                    mapper.mapChildToParent(entry, model);
                }
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}