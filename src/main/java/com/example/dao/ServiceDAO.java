package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Service;

public class ServiceDAO extends AbstractDAO<Service> {

    @Override
    protected Service mapRowToObject(ResultSet resultSet) throws Exception {
        return new Service(
                resultSet.getInt("ServiceID"),
                resultSet.getInt("CarID"),
                resultSet.getInt("CustomerID"),
                resultSet.getDate("ServiceDate"),
                resultSet.getString("ServiceDescription"),
                resultSet.getDouble("Cost"));
    }

    public List<Service> getAllServices() {
        return getAll("SELECT * FROM services");
    }

    public boolean updateService(Service service) throws SQLException {
        String sql = "UPDATE services SET CarID = ?, CustomerID = ?, ServiceDate = ?, ServiceDescription = ?, Cost = ? WHERE ServiceID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, service.getCarID());
            statement.setInt(2, service.getCustomerID());
            statement.setDate(3, new java.sql.Date(service.getServiceDate().getTime()));
            statement.setString(4, service.getServiceDescription());
            statement.setDouble(5, service.getCost());
            statement.setInt(6, service.getServiceID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean insertService(Service service) throws SQLException {
        String sql = "INSERT INTO services (CarID, CustomerID, ServiceDate, ServiceDescription, Cost) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, service.getCarID());
            statement.setInt(2, service.getCustomerID());
            statement.setDate(3, new java.sql.Date(service.getServiceDate().getTime()));
            statement.setString(4, service.getServiceDescription());
            statement.setDouble(5, service.getCost());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedServiceID = generatedKeys.getInt(1);
                        service.setServiceID(generatedServiceID);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean deleteService(int serviceID) throws SQLException {
        String sql = "DELETE FROM services WHERE ServiceID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, serviceID);
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

    public List<String> getAllServiceIDs() throws SQLException {
        String sql = "SELECT DISTINCT ServiceID FROM services ORDER BY ServiceID ASC";
        List<String> serviceIDs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                serviceIDs.add(String.valueOf(rs.getInt("ServiceID")));
            }
        }

        return serviceIDs;
    }

    // Additional methods to retrieve related data if needed
    // For example, getCarDetails, getCustomerDetails
}
