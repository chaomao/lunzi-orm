import com.thoughtworks.orm.ModelFinder;
import org.junit.After;
import org.junit.Test;
import test.model.Author;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthorTest {

    @Test
    public void should_save_author_into_db() {
        Author author = new Author("Mao Chao");
        author.save();

        Author result = ModelFinder.findById(Author.class, author.getId());

        assertThat(result, is(author));
        assertThat(result.getId(), is(author.getId()));
    }

    @After
    public void tearDown() throws Exception {
        getConnection("jdbc:mysql://localhost:3306/orm?user=root").createStatement().execute("TRUNCATE author");
    }
}
