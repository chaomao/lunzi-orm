package com.thoughtworks.orm;

import com.thoughtworks.orm.model.Author;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DataSetTest extends DBTest {
    @Test
    public void should_create_data_set() throws SQLException {

        Author author = new Author();
        author.save();
        DataSet dataSet = ConnectionManager.getDataSet("SELECT * FROM author");

        DataRow row = new DataRow();
        row.put("id", author.getId());
        row.put("name", null);

        for (DataRow dataRow : dataSet) {
            assertThat(dataRow, is(row));
        }
    }
}
