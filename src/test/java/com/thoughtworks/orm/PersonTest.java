package com.thoughtworks.orm;

import com.google.common.collect.Lists;
import com.thoughtworks.orm.finder.ModelFinder;
import com.thoughtworks.orm.model.Gender;
import com.thoughtworks.orm.model.Person;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersonTest extends DBTest {

    @Test
    public void should_save_int_string_enum_into_db() {
        Person person = new Person(28, "Mao Chao", Gender.Man, Lists.newArrayList("1", "2"));
        person.save();

        Person result = ModelFinder.findById(Person.class, person.getId());

        assertThat(result, is(person));
    }

    @Test
    public void should_return_all_persons() {
        List<Person> persons = Lists.newArrayList(
                new Person(26, "Mao", Gender.Man, Lists.newArrayList("123", "456")),
                new Person(27, "Chao", Gender.Man, Lists.newArrayList("789", "012"))
        );
        for (Person person : persons) {
            person.save();
        }
        List<Person> result = ModelFinder.findAll(Person.class);

        assertThat(result, is(persons));
    }
}
