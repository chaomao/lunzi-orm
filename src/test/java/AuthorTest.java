import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.ModelFinder;
import org.junit.After;
import org.junit.Test;
import test.model.Author;
import test.model.DBTest;

import java.sql.Connection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthorTest extends DBTest{

    @Test
    public void should_save_author_into_db() {
        Author author = new Author("Mao Chao");
        author.save();

        Author result = ModelFinder.findById(Author.class, author.getId());

        assertThat(result, is(author));
        assertThat(result.getId(), is(author.getId()));
    }
}
