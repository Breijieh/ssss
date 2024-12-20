package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Order;

public class OrderDAO extends AbstractDAO<Order> {

    @Override
    protected Order mapRowToObject(ResultSet resultSet) throws Exception {
        return new Order(
                resultSet.getInt("OrderID"),
                resultSet.getDate("OrderDate"),
                resultSet.getInt("CarID"),
                resultSet.getInt("CustomerID"),
                resultSet.getInt("EmployeeID"),
                resultSet.getInt("Quantity"),
                resultSet.getDouble("TotalPrice"));
    }

    public List<Order> getAllOrders() {
        return getAll("SELECT * FROM orders");
    }

    public boolean updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET OrderDate = ?, CarID = ?, CustomerID = ?, EmployeeID = ?, Quantity = ?, TotalPrice = ? WHERE OrderID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
            statement.setInt(2, order.getCarID());
            statement.setInt(3, order.getCustomerID());
            statement.setInt(4, order.getEmployeeID());
            statement.setInt(5, order.getQuantity());
            statement.setDouble(6, order.getTotalPrice());
            statement.setInt(7, order.getOrderID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (OrderDate, CarID, CustomerID, EmployeeID, Quantity, TotalPrice) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
            statement.setInt(2, order.getCarID());
            statement.setInt(3, order.getCustomerID());
            statement.setInt(4, order.getEmployeeID());
            statement.setInt(5, order.getQuantity());
            statement.setDouble(6, order.getTotalPrice());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedOrderID = generatedKeys.getInt(1);
                        order.setOrderID(generatedOrderID);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean deleteOrder(int orderID) throws SQLException {
        String sql = "DELETE FROM orders WHERE OrderID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";
        Map<String, String> columns = new HashMap<>();
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                columns.put(rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE"));
            }
        }
        return columns;
    }

    public List<String> getAllOrderIDs() throws SQLException {
        String sql = "SELECT DISTINCT OrderID FROM orders ORDER BY OrderID ASC";
        List<String> orderIDs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                orderIDs.add(String.valueOf(rs.getInt("OrderID")));
            }
        }

        return orderIDs;
    }

    public Order getOrderById(int orderID) throws SQLException {
        String sql = "SELECT * FROM orders WHERE OrderID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapRowToObject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, you can throw a custom exception or handle it as per your
            // application's requirement
        }
        return null;
    }

    public List<Order> getOrdersByEmployeeId(int employeeID) throws SQLException {
        String sql = "SELECT * FROM orders WHERE EmployeeID = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                orders.add(mapRowToObject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }
    // Additional methods to retrieve related data if needed
    // For example, getCarDetails, getCustomerDetails, getEmployeeDetails
}
