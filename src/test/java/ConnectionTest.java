import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConnectionTest {

    private static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        connection = getConnection("jdbc:mysql://localhost:3306/orm?user=root");
    }

    @Test
    public void should_insert_and_select_data_from_db_in_plain_sql() {
        try {
            String insertQuery = "INSERT INTO author values(%d,'%s')";
            connection.createStatement().executeUpdate(String.format(insertQuery, 0, "maochao"));

            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM author";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                assertThat(resultSet.getString(1), is("0"));
                assertThat(resultSet.getString(2), is("maochao"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        connection.createStatement().execute("TRUNCATE author");
    }
}
