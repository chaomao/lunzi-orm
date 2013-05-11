package com.thoughtworks.orm.finder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.thoughtworks.orm.DataRow;
import com.thoughtworks.orm.DataSet;
import com.thoughtworks.orm.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.*;
import static com.thoughtworks.orm.ConnectionManager.getDataSet;
import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFactory.createModelWithoutAssociation;
import static com.thoughtworks.orm.finder.ModelFactory.setChildren;

class AssociationSetter {

    private final List<Model> parents;
    private Mapper mapper;

    public AssociationSetter(List<Model> parents, Mapper mapper) {
        this.parents = parents;
        this.mapper = mapper;
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

    private Map<Integer, List<Model>> getChildrenMap() throws SQLException, InstantiationException, IllegalAccessException {
        Object[] parentIds = getParentId();
        DataSet dataSet = getDataSet(getWhereQuery(mapper.getAssociationClass(), mapper.getForeignKey(), parentIds), parentIds);
        HashMap<Integer, List<Model>> resultMap = getResultMapFromResult(dataSet);
        updateChildrenAssociation(resultMap);
        return resultMap;
    }

    private HashMap<Integer, List<Model>> getResultMapFromResult(DataSet dataSet) {
        HashMap<Integer, List<Model>> resultMap = new HashMap<>();
        for (DataRow row : dataSet) {
            Model child = createModelWithoutAssociation(mapper.getAssociationClass(), row);
            Integer parentId = (Integer) row.getObject(mapper.getForeignKey());
            pushChildIntoMap(resultMap, child, parentId);
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
}