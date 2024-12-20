package com.example.service;

import com.example.dao.EmployeeDAO;
import com.example.model.Employee;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EmployeeService {
    private EmployeeDAO employeeDAO;
    private ExportUtil exportUtil;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.exportUtil = new ExportUtil();
    }

    /**
     * Retrieves column names and their data types for the employees table.
     *
     * @return A map of column names to their data types.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, String> getColumnNamesAndTypes() throws SQLException {
        return employeeDAO.getColumnNamesAndTypes("employees");
    }

    /**
     * Retrieves all employees from the database.
     *
     * @return List of all Employee objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getAllEmployees();
    }

    /**
     * Updates an existing employee in the database.
     *
     * @param employee The Employee object with updated information.
     * @return True if the update was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean updateEmployee(Employee employee) throws SQLException {
        return employeeDAO.updateEmployee(employee);
    }

    /**
     * Inserts a new employee into the database.
     *
     * @param employee The Employee object to insert.
     * @return True if the insertion was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean insertEmployee(Employee employee) throws SQLException {
        return employeeDAO.insertEmployee(employee);
    }

    /**
     * Deletes an employee from the database by EmployeeID.
     *
     * @param employeeID The ID of the employee to delete.
     * @return True if the deletion was successful; otherwise, false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteEmployee(int employeeID) throws SQLException {
        return employeeDAO.deleteEmployee(employeeID);
    }

    /**
     * Exports a list of employees to a CSV file.
     *
     * @param employees   The list of employees to export.
     * @param destination The destination file to save the CSV.
     * @throws IOException If an I/O error occurs.
     */
    public void exportEmployeesToCSV(List<Employee> employees, File destination) throws IOException {
        exportUtil.exportEmployeesToCSV(employees, destination);
    }

    public Employee getEmployeeById(int employeeID) throws SQLException {
        return employeeDAO.getEmployeeById(employeeID);
    }
    
}
