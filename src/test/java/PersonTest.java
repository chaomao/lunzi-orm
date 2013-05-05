import com.thoughtworks.orm.ModelFinder;
import org.junit.Test;
import test.model.Gender;
import test.model.Person;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PersonTest extends DBTest {

    @Test
    public void should_save_int_string_enum_into_db() {
        Person person = new Person(28, "Mao Chao", Gender.Man);
        person.save();

        Person result = ModelFinder.findById(Person.class, person.getId());

        assertThat(result, is(person));
    }
}
