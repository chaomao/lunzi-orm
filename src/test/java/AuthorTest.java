
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AuthorTest {
    private static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        connection = getConnection("jdbc:mysql://localhost:3306/orm?user=root");
    }

    @Test
    public void should_get_author_from_db_using_static_find() throws Exception {
        try {
            String insertQuery = "INSERT INTO author values(%d,'%s')";
            connection.createStatement().executeUpdate(String.format(insertQuery, 1, "MaoChao"));

            Author author = Author.find(1);

            assertThat(author.getId(), is(1));
            assertThat(author.getName(), is("MaoChao"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        connection.createStatement().execute("TRUNCATE author");
    }
}
