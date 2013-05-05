package test.model;

import com.thoughtworks.orm.ORMModel;
import com.thoughtworks.orm.annotation.Column;

public class Person extends ORMModel {
    @Column
    private int age;
    @Column
    private String name;
    @Column
    private Sex sex;

    public Person() {
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    public Person(int age, String name, Sex sex) {
        this.age = age;
        this.name = name;
        this.sex = sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        if (!name.equals(person.name)) return false;
        if (sex != person.sex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + name.hashCode();
        result = 31 * result + sex.hashCode();
        return result;
    }
}
