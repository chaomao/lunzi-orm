public class Author extends Model {

    @Column
    private String name;

    public Author() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
