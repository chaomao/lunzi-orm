package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFinder.getModels;

abstract class AssociationSetter {

    protected ArrayList<Model> getChildrenWithEagerLoading(Class childType, String foreignKey, Object... parentIds) throws SQLException, InstantiationException, IllegalAccessException {
        String whereQuery = getWhereQuery(childType, foreignKey, parentIds);
        return getModels(childType, whereQuery, parentIds);
    }

    public abstract void process(List<Model> models, Field field);
}
