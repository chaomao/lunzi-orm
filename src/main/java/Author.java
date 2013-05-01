import static com.google.common.collect.Iterables.filter;
import static java.sql.DriverManager.getConnection;

public class Author extends Model {

    @Column
    private String name;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public static Author find(int id) throws Exception {
        final Author author = new Author();
        return (Author) find_2(id, author);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
