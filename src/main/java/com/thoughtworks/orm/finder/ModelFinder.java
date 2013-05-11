package com.thoughtworks.orm.finder;

import java.util.List;

import static com.thoughtworks.orm.QueryGenerator.getFindAllQuery;
import static com.thoughtworks.orm.QueryGenerator.getFindByIdQuery;
import static com.thoughtworks.orm.finder.ModelFactory.getModels;

public class ModelFinder {

    public static <T> T findById(Class<T> modelClass, int id) {
        String findByIdQuery = getFindByIdQuery(modelClass);
        return (T) getModels(modelClass, findByIdQuery, id).get(0);
    }

    public static <T> List<T> findAll(Class<T> modelClass) {
        String findAllQuery = getFindAllQuery(modelClass);
        return (List<T>) getModels(modelClass, findAllQuery);
    }
}
