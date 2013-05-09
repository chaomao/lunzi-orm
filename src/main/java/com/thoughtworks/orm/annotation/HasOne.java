package com.thoughtworks.orm.annotation;

import com.thoughtworks.orm.Model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface HasOne {
    String foreignKey();

    Class<? extends Model> klass();
}
