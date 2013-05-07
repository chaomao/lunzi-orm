package com.thoughtworks.orm;

import org.junit.Test;
import com.thoughtworks.orm.model.Author;

import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ModelFinderTest extends DBTest {

    @Test
    public void should_get_author_from_db_using_static_find(){
        try {
            String insertQuery = "INSERT INTO author values(%d,'%s')";
            DBTest.connection.createStatement().executeUpdate(String.format(insertQuery, 1, "MaoChao"));

            Author author = ModelFinder.findById(Author.class, 1);

            assertThat(author.getId(), is(1));
            assertThat(author.getName(), is("MaoChao"));
        } catch (SQLException e) {
        }
    }
}
