package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;
import com.thoughtworks.orm.ModelHelper;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.thoughtworks.orm.QueryGenerator.getWhereQuery;
import static com.thoughtworks.orm.finder.ModelFinder.getModels;

abstract class AssociationSetter {

    public abstract void process(Object model, Field field, int parentId);

    protected ArrayList<Model> getChildren(Class childType, String foreignKey, int parentId) throws SQLException, InstantiationException, IllegalAccessException {
        String criteria = String.format("%s = ?", foreignKey);
        String whereQuery = getWhereQuery(ModelHelper.getTableName(childType), criteria);
        return getModels(childType, whereQuery, parentId);
    }
}
