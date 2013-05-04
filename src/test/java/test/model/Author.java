package test.model;

import com.thoughtworks.orm.annotation.Column;
import com.thoughtworks.orm.Model;

public class Author extends Model {

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
