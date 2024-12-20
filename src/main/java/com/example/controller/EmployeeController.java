package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.DetailsDialog;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.model.Employee;
import com.example.model.Order;
import com.example.service.EmployeeService;
import com.example.service.OrderService;
import com.example.AppStructure.AppStage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class EmployeeController {
    private EmployeeService employeeService;
    private OrderService orderService;
    private ObservableList<Employee> employeeList;
    private FilteredList<Employee> filteredEmployeeList;
    private final String regex = ".*[a-zA-Z].*";

    public EmployeeController() {
        this.employeeService = new EmployeeService();
        this.orderService = new OrderService();
        this.employeeList = FXCollections.observableArrayList();
        this.filteredEmployeeList = new FilteredList<>(employeeList, p -> true);
    }

    /**
     * Handles the edit action for an employee.
     *
     * @param employee The Employee object to edit.
     */
    public void handleEdit(Employee employee) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("firstName", "First Name", "VARCHAR", employee.getFirstName()));
        fieldDefinitions.add(new FieldDefinition("lastName", "Last Name", "VARCHAR", employee.getLastName()));
        fieldDefinitions.add(new FieldDefinition("position", "Position", "VARCHAR", employee.getPosition()));
        fieldDefinitions.add(new FieldDefinition("salary", "Salary", "DOUBLE", employee.getSalary()));
        fieldDefinitions.add(new FieldDefinition("hireDate", "Hire Date", "DATE", employee.getSimpleDate()));

        AppStage.getInstance().showModal(
                employee,
                fieldDefinitions,
                "Edit Employee",
                "Update",
                () -> {
                    try {
                        boolean success = employeeService.updateEmployee(employee);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Employee updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the employee.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the employee:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    // Cancel action
                });
    }

    /**
     * Handles the delete action for an employee.
     *
     * @param employee The Employee object to delete.
     */
    /**
     * Handles the insert action for adding a new employee.
     */
    public void handleInsert() {
        Employee newEmployee = new Employee(0, "", "", "", 0.0, new Date());

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("firstName", "First Name", "VARCHAR", newEmployee.getFirstName()),
                new FieldDefinition("lastName", "Last Name", "VARCHAR", newEmployee.getLastName()),
                new FieldDefinition("position", "Position", "VARCHAR", newEmployee.getPosition()),
                new FieldDefinition("salary", "Salary", "DOUBLE", newEmployee.getSalary()),
                new FieldDefinition("hireDate", "Hire Date", "DATE", newEmployee.getSimpleDate()));

        AppStage.getInstance().showModal(
                newEmployee,
                fieldDefinitions,
                "Add New Employee",
                "Add",
                () -> {
                    try {
                        if (employeeService.insertEmployee(newEmployee)) {
                            employeeList.add(newEmployee);
                            AppStage.getInstance().showMessage("Success", "New employee added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new employee.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        AppStage.getInstance().showMessage("Error", "Database Error: " + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    System.out.println("Insertion cancelled.");
                });
    }

    public ObservableList<Employee> loadAllEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            employeeList.setAll(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading employees:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredEmployeeList;
    }

    /**
     * Retrieves the search boxes for filtering employees.
     *
     * @return A list of CustomSearchBox objects.
     */
    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = employeeService.getColumnNamesAndTypes();
            return columns.entrySet().stream()
                    .map(entry -> new CustomSearchBox(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load search criteria:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
            return new ArrayList<>();
        }
    }

    /**
     * Resets all filters applied to the employee list.
     */
    public void resetFilters() {
        filteredEmployeeList.setPredicate(employee -> true);
    }

    /**
     * Handles the export action to save employees as a CSV file.
     */
    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("employee_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                employeeService.exportEmployeesToCSV(filteredEmployeeList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Employees have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting employees:\n" + e.getMessage(),
                        MessageDialog.MessageType.ERROR);
            }
        }
    }

    /**
     * Retrieves the ObservableList of employees.
     *
     * @return The filtered list of employees.
     */
    public ObservableList<Employee> getEmployeeList() {
        return filteredEmployeeList;
    }

    /**
     * Applies filters to the employee list based on the provided search criteria.
     *
     * @param searchBoxes The list of search criteria.
     */
    public void filterEmployees(List<CustomSearchBox> searchBoxes) {
        filteredEmployeeList.setPredicate(employee -> {
            for (CustomSearchBox searchBox : searchBoxes) {
                String dataType = searchBox.getDataType().toUpperCase();
                String input = searchBox.getText().trim();

                if (input.isEmpty()) {
                    searchBox.clearValidation();
                    continue;
                }

                boolean inputValid = isInputValid(dataType, input);
                if (!inputValid) {
                    searchBox.setValid(false);
                    return false;
                } else {
                    searchBox.setValid(true);
                    boolean matches = applyFilter(dataType, input, employee.getFieldValue(searchBox.getColumnText()));
                    if (!matches) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    /**
     * Validates the input based on the data type.
     *
     * @param dataType The data type of the field.
     * @param input    The user input.
     * @return True if valid; otherwise, false.
     */
    private boolean isInputValid(String dataType, String input) {
        switch (dataType) {
            case "INT":
                return isIntInputValid(input);
            case "DOUBLE":
            case "DECIMAL":
                return isDoubleInputValid(input);
            case "DATE":
                return isDateInputValid(input);
            default:
                return true;
        }
    }

    /**
     * Applies the appropriate filter based on the data type.
     *
     * @param dataType   The data type of the field.
     * @param input      The user input.
     * @param fieldValue The value of the field in the Employee object.
     * @return True if the field matches the filter; otherwise, false.
     */
    private boolean applyFilter(String dataType, String input, String fieldValue) {
        switch (dataType) {
            case "INT":
                return safeFilterInt(input, fieldValue);
            case "DOUBLE":
            case "DECIMAL":
                return safeFilterDecimal(input, fieldValue);
            case "DATE":
                return safeFilterDate(input, fieldValue);
            default:
                return fuzzyMatch(fieldValue, input);
        }
    }

    private String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(currentDate);
    }

    /**
     * Performs a fuzzy match between the field value and the input.
     *
     * @param fieldValue The value of the field in the Employee object.
     * @param input      The user input.
     * @return True if the fuzzy match ratio is >= 60; otherwise, false.
     */
    private boolean fuzzyMatch(String fieldValue, String input) {
        return FuzzySearch.ratio(fieldValue.toLowerCase(), input.toLowerCase()) >= 60;
    }

    /**
     * Safely applies integer-based filters.
     *
     * @param input      The user input.
     * @param fieldValue The value of the field in the Employee object.
     * @return True if the field matches the filter; otherwise, false.
     */
    private boolean safeFilterInt(String input, String fieldValue) {
        try {
            int actualValue = Integer.parseInt(fieldValue);
            return applyIntFilter(input, actualValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Safely applies decimal-based filters.
     *
     * @param input      The user input.
     * @param fieldValue The value of the field in the Employee object.
     * @return True if the field matches the filter; otherwise, false.
     */
    private boolean safeFilterDecimal(String input, String fieldValue) {
        try {
            double actualValue = Double.parseDouble(fieldValue);
            return applyDecimalFilter(input, actualValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Safely applies date-based filters.
     *
     * @param input      The user input.
     * @param fieldValue The value of the field in the Employee object.
     * @return True if the field matches the filter; otherwise, false.
     */
    private boolean safeFilterDate(String input, String fieldValue) {
        try {
            // Assuming input is in format YYYY-MM-DD
            java.time.LocalDate inputDate = java.time.LocalDate.parse(input);
            java.time.LocalDate actualDate = java.time.LocalDate.parse(fieldValue);
            return actualDate.equals(inputDate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Applies integer-based filtering logic.
     *
     * @param input       The user input.
     * @param actualValue The actual integer value from the Employee object.
     * @return True if the actual value matches the filter criteria; otherwise,
     *         false.
     */
    private boolean applyIntFilter(String input, int actualValue) {
        if (input.endsWith("-")) {
            int lowerBound = Integer.parseInt(input.replace("-", "").trim());
            return actualValue >= lowerBound;
        }
        if (input.startsWith("-")) {
            int upperBound = Integer.parseInt(input.replace("-", "").trim());
            return actualValue <= upperBound;
        }
        if (input.contains("-")) {
            String[] range = input.split("-");
            if (range.length != 2)
                return false;
            int lowerBound = Integer.parseInt(range[0].trim());
            int upperBound = Integer.parseInt(range[1].trim());
            return actualValue >= lowerBound && actualValue <= upperBound;
        }
        int exactValue = Integer.parseInt(input.trim());
        return actualValue == exactValue;
    }

    /**
     * Applies decimal-based filtering logic.
     *
     * @param input       The user input.
     * @param actualValue The actual double value from the Employee object.
     * @return True if the actual value matches the filter criteria; otherwise,
     *         false.
     */
    private boolean applyDecimalFilter(String input, double actualValue) {
        if (input.endsWith("-")) {
            double lowerBound = Double.parseDouble(input.replace("-", "").trim());
            return actualValue >= lowerBound;
        }
        if (input.startsWith("-")) {
            double upperBound = Double.parseDouble(input.replace("-", "").trim());
            return actualValue <= upperBound;
        }
        if (input.contains("-")) {
            String[] range = input.split("-");
            if (range.length != 2)
                return false;
            double lowerBound = Double.parseDouble(range[0].trim());
            double upperBound = Double.parseDouble(range[1].trim());
            return actualValue >= lowerBound && actualValue <= upperBound;
        }
        double exactValue = Double.parseDouble(input.trim());
        return actualValue == exactValue;
    }

    /**
     * Validates integer-based input.
     *
     * @param input The user input.
     * @return True if valid; otherwise, false.
     */
    private boolean isIntInputValid(String input) {
        if (input.matches(regex))
            return false;
        try {
            if (input.equals("-"))
                return false;
            if (input.endsWith("-")) {
                Integer.parseInt(input.replace("-", "").trim());
                return true;
            }
            if (input.startsWith("-")) {
                if (input.length() == 1)
                    return false;
                Integer.parseInt(input.replace("-", "").trim());
                return true;
            }
            if (input.contains("-")) {
                String[] range = input.split("-");
                if (range.length == 2) {
                    Integer.parseInt(range[0].trim());
                    Integer.parseInt(range[1].trim());
                    return true;
                }
                return false;
            }

            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates double-based input.
     *
     * @param input The user input.
     * @return True if valid; otherwise, false.
     */
    private boolean isDoubleInputValid(String input) {
        if (input.matches(regex))
            return false;
        try {
            if (input.equals("-"))
                return false;
            if (input.endsWith("-")) {
                Double.parseDouble(input.replace("-", "").trim());
                return true;
            }
            if (input.startsWith("-")) {
                if (input.length() == 1)
                    return false;
                Double.parseDouble(input.replace("-", "").trim());
                return true;
            }
            if (input.contains("-")) {
                String[] range = input.split("-");
                if (range.length == 2) {
                    Double.parseDouble(range[0].trim());
                    Double.parseDouble(range[1].trim());
                    return true;
                }
                return false;
            }

            Double.parseDouble(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates date-based input.
     *
     * @param input The user input.
     * @return True if valid; otherwise, false.
     */
    private boolean isDateInputValid(String input) {
        try {
            // Assuming input is in format YYYY-MM-DD
            java.time.LocalDate.parse(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Handles the details action for an employee.
     *
     * @param employee The Employee object whose details are to be displayed.
     */
    public void handleDetails(Employee employee) {
        try {
            // Implement details view for Employee
            // For example, show related Orders if applicable

            showEmployeeDetails(employee);
        } catch (Exception e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load details for employee:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    private void showEmployeeDetails(Employee employee) {
        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Details for Employee ID: " + employee.getEmployeeID());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Employee Info
        VBox employeeInfoBox = new VBox(5);
        Label employeeIDLabel = new Label("Employee ID: " + employee.getEmployeeID());
        Label firstNameLabel = new Label("First Name: " + employee.getFirstName());
        Label lastNameLabel = new Label("Last Name: " + employee.getLastName());
        Label positionLabel = new Label("Position: " + employee.getPosition());
        Label salaryLabel = new Label(String.format("Salary: $%.2f", employee.getSalary()));
        Label hireDateLabel = new Label("Hire Date: " + employee.getSimpleDate());

        employeeInfoBox.getChildren().addAll(employeeIDLabel, firstNameLabel, lastNameLabel,
                positionLabel, salaryLabel, hireDateLabel);

        // Related Orders Info
        VBox ordersBox = new VBox(5);
        Label ordersLabel = new Label("Orders Managed:");
        ordersBox.getChildren().add(ordersLabel);

        try {
            List<Order> orders = orderService.getOrdersByEmployeeId(employee.getEmployeeID());

            if (orders.isEmpty()) {
                Label noOrdersLabel = new Label("No orders managed by this employee.");
                ordersBox.getChildren().add(noOrdersLabel);
            } else {
                TableView<Order> ordersTable = createOrdersTableView();
                ObservableList<Order> ordersData = FXCollections.observableArrayList(orders);
                ordersTable.setItems(ordersData);
                ordersTable.setPrefHeight(200); // Adjust height as needed

                ordersBox.getChildren().add(ordersTable);
            }
        } catch (SQLException e) {
            System.out.println("Cannot display orders details for this employee");
            e.printStackTrace();
            Label errorLabel = new Label("Error fetching orders: " + e.getMessage());
            ordersBox.getChildren().add(errorLabel);
        }

        contentBox.getChildren().addAll(employeeInfoBox, ordersBox);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }

    /**
     * Creates and configures a TableView for displaying orders.
     *
     * @return Configured TableView<Order> instance.
     */
    private TableView<Order> createOrdersTableView() {
        TableView<Order> ordersTable = new TableView<>();

        // Define table columns
        TableColumn<Order, Number> orderIDCol = new TableColumn<>("Order ID");
        orderIDCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        orderIDCol.setPrefWidth(80);

        TableColumn<Order, Date> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderDateCol.setPrefWidth(120);

        TableColumn<Order, Number> carIDCol = new TableColumn<>("Car ID");
        carIDCol.setCellValueFactory(new PropertyValueFactory<>("carID"));
        carIDCol.setPrefWidth(80);

        TableColumn<Order, Number> customerIDCol = new TableColumn<>("Customer ID");
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerIDCol.setPrefWidth(100);

        TableColumn<Order, Number> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(80);

        TableColumn<Order, Number> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setPrefWidth(100);

        // Add columns to the table
        ordersTable.getColumns().addAll(orderIDCol, orderDateCol, carIDCol, customerIDCol, quantityCol, totalPriceCol);

        // Optional: Customize table appearance
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.getStyleClass().add("table-view"); // Ensure your CSS styles this class appropriately

        return ordersTable;
    }

}
