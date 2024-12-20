/*generic methods for database operations.*/
package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDAO<T> {

    // convert resultSet form database to model (like car, user)
    protected abstract T mapRowToObject(ResultSet resultSet) throws Exception;

    public List<T> getAll(String query) {
        List<T> list = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                list.add(mapRowToObject(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public T getOne(String query, Object... params) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = createPreparedStatement(connection, query, params);
                ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return mapRowToObject(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // for quires
    private PreparedStatement createPreparedStatement(Connection connection, String query, Object... params)
            throws Exception {
        PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (Object param : params) {
            statement.setObject(index++, param);
        }
        return statement;
    }

    /*
    used in contoller to create dynamic searchboxes
     * output:
     * "CarID": "INT",
     * "Make": "VARCHAR",
     * "Model": "VARCHAR",
     * "Year": "INT",
     * "Price": "DOUBLE",
     * "Stock": "INT",
     * "VIN": "VARCHAR"
     */
    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        Map<String, String> columnData = new HashMap<>();
        String query = "SELECT * FROM " + tableName + " WHERE 1=0";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnType = metaData.getColumnTypeName(i);
                columnData.put(columnName, columnType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnData;
    }
}
