import com.thoughtworks.orm.ConnectionManager;
import org.junit.After;
import org.junit.BeforeClass;

import java.sql.Connection;

public abstract class DBTest {
    protected static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        connection = ConnectionManager.getDBConnection();
    }

    @After
    public void tearDown() throws Exception {
        connection.createStatement().execute("TRUNCATE author");
        connection.close();
    }
}
