package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Payment;

public class PaymentDAO extends AbstractDAO<Payment> {

    @Override
    protected Payment mapRowToObject(ResultSet resultSet) throws Exception {
        return new Payment(
                resultSet.getInt("PaymentID"),
                resultSet.getDate("PaymentDate"),
                resultSet.getString("PaymentMethod"),
                resultSet.getDouble("Amount"),
                resultSet.getInt("OrderID"));
    }

    public List<Payment> getAllPayments() {
        return getAll("SELECT * FROM payments");
    }

    public boolean updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET PaymentDate = ?, PaymentMethod = ?, Amount = ?, OrderID = ? WHERE PaymentID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, new java.sql.Date(payment.getPaymentDate().getTime()));
            statement.setString(2, payment.getPaymentMethod());
            statement.setDouble(3, payment.getAmount());
            statement.setInt(4, payment.getOrderID());
            statement.setInt(5, payment.getPaymentID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (PaymentDate, PaymentMethod, Amount, OrderID) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setDate(1, new java.sql.Date(payment.getPaymentDate().getTime()));
            statement.setString(2, payment.getPaymentMethod());
            statement.setDouble(3, payment.getAmount());
            statement.setInt(4, payment.getOrderID());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedPaymentID = generatedKeys.getInt(1);
                        payment.setPaymentID(generatedPaymentID);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean deletePayment(int paymentID) throws SQLException {
        String sql = "DELETE FROM payments WHERE PaymentID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, paymentID);
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

    public List<String> getAllPaymentIDs() throws SQLException {
        String sql = "SELECT DISTINCT PaymentID FROM payments ORDER BY PaymentID ASC";
        List<String> paymentIDs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                paymentIDs.add(String.valueOf(rs.getInt("PaymentID")));
            }
        }

        return paymentIDs;
    }

    // Additional methods to retrieve related data if needed
    // For example, getOrderDetails
}
