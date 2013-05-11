package com.thoughtworks.orm;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class DataSet implements Iterable<DataRow> {

    private ArrayList<DataRow> dataRows = new ArrayList<>();

    public static DataSet createByResultSet(ResultSet resultSet) {
        try {
            DataSet dataSet = new DataSet();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                DataRow row = new DataRow();
                for (int i = 0; i < columnCount; i++) {
                    row.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
                }
                dataSet.dataRows.add(row);
            }
            return dataSet;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public Iterator<DataRow> iterator() {
        return dataRows.iterator();
    }
}
