package com.thoughtworks.orm.finder;

import com.thoughtworks.orm.Model;

import java.util.List;
import java.util.Map;

public interface Mapper {
    String getForeignKey();

    Class<?> getAssociationClass();

    void mapChildToParent(Map.Entry<Integer, List<Model>> entry, Model model) throws IllegalAccessException;
}
