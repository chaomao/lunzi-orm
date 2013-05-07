package com.thoughtworks.orm.model;

import com.thoughtworks.orm.Model;

import java.util.ArrayList;

public class Person extends Model {
    private int age;
    private String name;
    private Gender gender;
    private ArrayList<String> telephoneNumbers;

    public Person() {
    }

    public Person(int age, String name, Gender gender, ArrayList<String> telephoneNumbers) {
        this.age = age;
        this.name = name;
        this.gender = gender;
        this.telephoneNumbers = telephoneNumbers;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (age != person.age) return false;
        if (gender != person.gender) return false;
        if (!name.equals(person.name)) return false;
        if (!telephoneNumbers.equals(person.telephoneNumbers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = age;
        result = 31 * result + name.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + telephoneNumbers.hashCode();
        return result;
    }
}
