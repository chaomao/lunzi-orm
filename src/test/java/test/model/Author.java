package test.model;

import com.thoughtworks.orm.ORMModel;
import com.thoughtworks.orm.annotation.Column;

public class Author extends ORMModel {

    @Column
    private String name;

    public Author() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
