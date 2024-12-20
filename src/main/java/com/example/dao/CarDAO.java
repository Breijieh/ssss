package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Car;
import com.example.model.CarServiceRecord;

public class CarDAO extends AbstractDAO<Car> {


    //resultSet is like a row in a table
    @Override
    protected Car mapRowToObject(ResultSet resultSet) throws Exception {
        return new Car(
                resultSet.getInt("CarID"),
                resultSet.getString("Make"),
                resultSet.getString("Model"),
                resultSet.getInt("Year"),
                resultSet.getDouble("Price"),
                resultSet.getInt("Stock"),
                resultSet.getString("VIN"));
    }

    public List<Car> getAllCars() {
        return getAll("SELECT * FROM cars");
    }


    //take a exitst car but updated (by setters)
    public boolean updateCar(Car car) throws SQLException {
        String sql = "UPDATE cars SET Make = ?, Model = ?, Year = ?, Price = ?, Stock = ?, VIN = ? WHERE CarID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, car.getMake());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getYear());
            statement.setDouble(4, car.getPrice());
            statement.setInt(5, car.getStock());
            statement.setString(6, car.getVin());
            statement.setInt(7, car.getCarID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        }
    }


    //create new car then pass it here to insert it
    public boolean insertCar(Car car) throws SQLException {
        String sql = "INSERT INTO cars (Make, Model, Year, Price, Stock, VIN) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, car.getMake());
            statement.setString(2, car.getModel());
            statement.setInt(3, car.getYear());
            statement.setDouble(4, car.getPrice());
            statement.setInt(5, car.getStock());
            statement.setString(6, car.getVin());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedCarID = generatedKeys.getInt(1);
                        car.setCarID(generatedCarID);
                    }
                }
                return true;
            }
            return false;

        }
    }

    public boolean deleteCar(int carID) throws SQLException {
        String sql = "DELETE FROM cars WHERE CarID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, carID);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        }
    }



    //the carid is in  the services table (ez)
    public List<CarServiceRecord> getServicesForCar(int carID) throws SQLException {
        String sql = "SELECT ServiceID, ServiceDate, ServiceDescription, Cost FROM services WHERE CarID = ? ORDER BY ServiceDate DESC";
        List<CarServiceRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, carID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int serviceID = rs.getInt("ServiceID");
                LocalDate date = rs.getDate("ServiceDate").toLocalDate();
                String desc = rs.getString("ServiceDescription"); 
                double cost = rs.getDouble("Cost");
                records.add(new CarServiceRecord(serviceID, date, desc, cost));
            }
        }
        return records;
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        //  يجيب اسم الكولوم ونوعه
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


    //used in reporets
    public List<String> getAllCarModels() throws SQLException {
        String sql = "SELECT DISTINCT Model FROM cars ORDER BY Model ASC";
        List<String> models = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                models.add(rs.getString("Model"));
            }
        }

        return models;
    }

    public Car getCarById(int carID) throws SQLException {
        String sql = "SELECT * FROM cars WHERE CarID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, carID);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapRowToObject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            
        }
        return null;
    }
}
