package test.model;

import com.thoughtworks.orm.ORMModel;
import com.thoughtworks.orm.annotation.Column;

public class Author extends ORMModel {

    @Column
    private String name;

    public Author(String name) {
        this.name = name;
    }

    public Author() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;

        Author author = (Author) o;

        return name.equals(author.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
