package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Employee;

public class EmployeeDAO extends AbstractDAO<Employee> {

    @Override
    protected Employee mapRowToObject(ResultSet resultSet) throws Exception {
        return new Employee(
                resultSet.getInt("EmployeeID"),
                resultSet.getString("FirstName"),
                resultSet.getString("LastName"),
                resultSet.getString("Position"),
                resultSet.getDouble("Salary"),
                resultSet.getDate("HireDate"));
    }

    /**
     * Retrieves all employees from the database.
     *
     * @return List of all Employee objects.
     */
    public List<Employee> getAllEmployees() {
        return getAll("SELECT * FROM employees");
    }

    /**
     * Updates an existing employee in the database.
     *
     * @param employee The Employee object with updated information.
     * @return True if the update was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET FirstName = ?, LastName = ?, Position = ?, Salary = ?, HireDate = ? WHERE EmployeeID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getPosition());
            statement.setDouble(4, employee.getSalary());
            statement.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));
            statement.setInt(6, employee.getEmployeeID());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Inserts a new employee into the database.
     *
     * @param employee The Employee object to insert.
     * @return True if the insertion was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insertEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (FirstName, LastName, Position, Salary, HireDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getFirstName());
            statement.setString(2, employee.getLastName());
            statement.setString(3, employee.getPosition());
            statement.setDouble(4, employee.getSalary());
            statement.setDate(5, new java.sql.Date(employee.getHireDate().getTime()));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedEmployeeID = generatedKeys.getInt(1);
                        employee.setEmployeeID(generatedEmployeeID);
                    }
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Deletes an employee from the database by EmployeeID.
     *
     * @param employeeID The ID of the employee to delete.
     * @return True if the deletion was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteEmployee(int employeeID) throws SQLException {
        String sql = "DELETE FROM employees WHERE EmployeeID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves column names and their data types for the specified table.
     *
     * @param tableName The name of the table.
     * @return A map of column names to their data types.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves all unique EmployeeIDs from the employees table.
     *
     * @return List of EmployeeID as Strings.
     * @throws SQLException If a database access error occurs.
     */
    public List<String> getAllEmployeeIDs() throws SQLException {
        String sql = "SELECT DISTINCT EmployeeID FROM employees ORDER BY EmployeeID ASC";
        List<String> employeeIDs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                employeeIDs.add(String.valueOf(rs.getInt("EmployeeID")));
            }
        }

        return employeeIDs;
    }

    public Employee getEmployeeById(int employeeID) throws SQLException {
        String sql = "SELECT * FROM employees WHERE EmployeeID = ?";
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employeeID);
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
    // Additional methods can be added here if needed
}
