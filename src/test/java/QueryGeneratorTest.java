import com.thoughtworks.orm.QueryGenerator;
import org.junit.Test;
import test.model.Author;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryGeneratorTest {

    @Test
    public void should_generate_insert_query() {
        String actual = QueryGenerator.insert(new Author());

        String expect = "INSERT INTO author (name) VALUES (?)";

        assertThat(actual, is(expect));
    }
}
