package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Customer;
import com.example.model.Payment;
import com.example.model.Service;

public class CustomerDAO extends AbstractDAO<Customer> {

    @Override
    protected Customer mapRowToObject(ResultSet resultSet) throws Exception {
        return new Customer(
                resultSet.getInt("CustomerID"),
                resultSet.getString("FirstName"),
                resultSet.getString("LastName"),
                resultSet.getString("Email"),
                resultSet.getString("Phone"),
                resultSet.getString("Address"),
                resultSet.getString("City"),
                resultSet.getString("State"),
                resultSet.getString("ZipCode"));
    }

    public List<Customer> getAllCustomers() {
        return getAll("SELECT * FROM customers");
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET FirstName = ?, LastName = ?, Email = ?, Phone = ?, Address = ?, City = ?, State = ?, ZipCode = ? WHERE CustomerID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPhone());
            statement.setString(5, customer.getAddress());
            statement.setString(6, customer.getCity());
            statement.setString(7, customer.getState());
            statement.setString(8, customer.getZipCode());
            statement.setInt(9, customer.getCustomerID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (FirstName, LastName, Email, Phone, Address, City, State, ZipCode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, customer.getFirstName());
            statement.setString(2, customer.getLastName());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getPhone());
            statement.setString(5, customer.getAddress());
            statement.setString(6, customer.getCity());
            statement.setString(7, customer.getState());
            statement.setString(8, customer.getZipCode());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedCustomerID = generatedKeys.getInt(1);
                        customer.setCustomerID(generatedCustomerID);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean deleteCustomer(int customerID) throws SQLException {
        String sql = "DELETE FROM customers WHERE CustomerID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<Service> getServicesForCustomer(int customerID) throws SQLException {
        String sql = "SELECT ServiceID, CarID, ServiceDate, ServiceDescription, Cost FROM services WHERE CustomerID = ? ORDER BY ServiceDate DESC";
        List<Service> services = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int serviceID = rs.getInt("ServiceID");
                int carID = rs.getInt("CarID");
                java.sql.Date serviceDate = rs.getDate("ServiceDate");
                String description = rs.getString("ServiceDescription");
                double cost = rs.getDouble("Cost");
                services.add(new Service(serviceID, carID, customerID, serviceDate, description, cost));
            }
        }
        return services;
    }

    public List<Payment> getPaymentsForCustomer(int customerID) throws SQLException {
        String sql = "SELECT p.PaymentID, p.PaymentDate, p.PaymentMethod, p.Amount, p.OrderID " +
                "FROM payments p " +
                "JOIN orders o ON p.OrderID = o.OrderID " +
                "WHERE o.CustomerID = ? " +
                "ORDER BY p.PaymentDate DESC";
        List<Payment> payments = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int paymentID = rs.getInt("PaymentID");
                java.sql.Date paymentDate = rs.getDate("PaymentDate");
                String paymentMethod = rs.getString("PaymentMethod");
                double amount = rs.getDouble("Amount");
                int orderID = rs.getInt("OrderID");
                payments.add(new Payment(paymentID, paymentDate, paymentMethod, amount, orderID));
            }
        }
        return payments;
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

    public List<String> getAllCustomerNames() throws SQLException {
        String sql = "SELECT DISTINCT CONCAT(FirstName, ' ', LastName) AS FullName FROM customers ORDER BY FullName ASC";
        List<String> names = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("FullName"));
            }
        }

        return names;
    }

    public Customer getCustomerById(int customerID) throws SQLException {
        String sql = "SELECT * FROM customers WHERE CustomerID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerID);
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
}
