
import com.thoughtworks.orm.ConnectionManager;
import com.thoughtworks.orm.ModelFinder;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import test.model.Author;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModelFinderTest {
    private static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        connection = ConnectionManager.getDBConnection();
    }

    @Test
    public void should_get_author_from_db_using_static_find(){
        try {
            String insertQuery = "INSERT INTO author values(%d,'%s')";
            connection.createStatement().executeUpdate(String.format(insertQuery, 1, "MaoChao"));

            Author author = ModelFinder.findById(Author.class, 1);

            assertThat(author.getId(), is(1));
            assertThat(author.getName(), is("MaoChao"));
        } catch (SQLException e) {
        }
    }

    @After
    public void tearDown() throws Exception {
        connection.createStatement().execute("TRUNCATE author");
        connection.close();
    }
}
