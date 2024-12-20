package com.example.views.Tables;

import com.example.model.Employee;

import java.sql.Date;

import com.example.components.general.StyledTableComponent;
import com.example.controller.EmployeeController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class EmployeeTableComponent extends StyledTableComponent<Employee> {
    private EmployeeController employeeController;

    public EmployeeTableComponent(EmployeeController employeeController) {
        super("Employee List");
        this.employeeController = employeeController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Employee, Number> employeeIDColumn = createColumn("Employee ID", "employeeID", 100);
        TableColumn<Employee, String> firstNameColumn = createColumn("First Name", "firstName", 120);
        TableColumn<Employee, String> lastNameColumn = createColumn("Last Name", "lastName", 120);
        TableColumn<Employee, String> positionColumn = createColumn("Position", "position", 150);
        TableColumn<Employee, Number> salaryColumn = createColumn("Salary", "salary", 100);
        TableColumn<Employee, Date> hireDateColumn = createColumn("Hire Date", "hireDate", 150);

        table.getColumns().addAll(
                employeeIDColumn,
                firstNameColumn,
                lastNameColumn,
                positionColumn,
                salaryColumn,
                hireDateColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Employee> employeeList) {
        table.setItems(employeeList);
    }

    @Override
    protected void onEdit(Employee employee) {
        employeeController.handleEdit(employee);
    }

    @Override
    protected void onDetails(Employee employee) {
        employeeController.handleDetails(employee);
    }

    @Override
    protected void onInsert() {
        employeeController.handleInsert();
    }

    public void exportToCSV() {
        employeeController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
