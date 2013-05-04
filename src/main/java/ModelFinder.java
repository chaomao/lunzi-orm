import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.google.common.collect.Iterables.filter;
import static java.sql.DriverManager.getConnection;

public class ModelFinder {

    public static Object findById(Class modelClass, int id) throws SQLException {
        try {
            Object object = modelClass.getConstructor().newInstance();
            String findByIDQuery = String.format("SELECT * FROM %s where id=%d", getTableName(object), id);
            ResultSet resultSet = getDBConnection().createStatement().executeQuery(findByIDQuery);
            Iterable<Field> annotatedColumns = getAnnotatedFields(object);
            return setObject(object, resultSet, annotatedColumns);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException();
        }
    }

    private static String getTableName(Object object) {
        return object.getClass().getSimpleName().toLowerCase();
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
