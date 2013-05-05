import com.thoughtworks.orm.ModelFinder;
import org.junit.Test;
import test.model.Person;
import test.model.Sex;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersonTest extends DBTest {

    @Test
    public void should_save_int_string_enum_into_db() {
        Person person = new Person(28, "Mao Chao", Sex.Man);
        person.save();

        Person result = ModelFinder.findById(Person.class, person.getId());

        assertThat(result, is(person));
    }
}
