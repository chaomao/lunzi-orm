import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.google.common.collect.Iterables.filter;
import static java.sql.DriverManager.getConnection;

public abstract class Model {
    @Column
    protected int id;

    protected static Object find_2(int id, Object object) throws SQLException {
        String findByIDQuery = String.format("SELECT * FROM %s where id=%d", object.getClass().getSimpleName().toLowerCase(), id);
        ResultSet resultSet = getDBConnection().createStatement().executeQuery(findByIDQuery);
        Iterable<Field> annotatedColumns = getAnnotatedFields(object);
        return setObject(object, resultSet, annotatedColumns);
    }

    private static Object setObject(Object object, ResultSet resultSet, Iterable<Field> annotatedColumns) throws SQLException {
        resultSet.next();
        for (Field input : annotatedColumns) {
            String columnName = getColumnName(input);
            Class<?> columnType = input.getType();
            try {
                Object value = resultSet.getObject(columnName, columnType);
                input.setAccessible(true);
                input.set(object, value);
            } catch (IllegalAccessException ignored) {
                ignored.printStackTrace();
            }
        }
        return object;
    }

    private static String getColumnName(Field input) {
        Column annotation = input.getAnnotation(Column.class);
        return annotation.value().isEmpty() ? input.getName() : annotation.value();
    }

    private static Iterable<Field> getAnnotatedFields(Object author) {
        try {
            ArrayList<Field> unfiltered = Lists.newArrayList(author.getClass().getDeclaredFields());
            unfiltered.add(author.getClass().getSuperclass().getDeclaredField("id"));
            return filter(unfiltered, new Predicate<Field>() {
                @Override
                public boolean apply(Field input) {
                    return input.isAnnotationPresent(Column.class);
                }
            });
        } catch (NoSuchFieldException e) {
            throw new RuntimeException();
        }
    }

    private static Connection getDBConnection() {
        try {
            return getConnection("jdbc:mysql://localhost:3306/orm?user=root");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
