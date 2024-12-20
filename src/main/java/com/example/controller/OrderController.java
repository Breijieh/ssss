package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.DetailsDialog;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.model.Order;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class OrderController {
    private OrderService orderService;
    private ObservableList<Order> orderList;
    private FilteredList<Order> filteredOrderList;
    private final String regex = ".*[a-zA-Z].*";

    public OrderController() {
        this.orderService = new OrderService();
        this.orderList = FXCollections.observableArrayList();
        this.filteredOrderList = new FilteredList<>(orderList, p -> true);
    }

    public void handleEdit(Order order) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("orderDate", "Order Date", "DATE", order.getSimpleDate()));
        fieldDefinitions.add(new FieldDefinition("carID", "Car ID", "INT", order.getCarID()));
        fieldDefinitions.add(new FieldDefinition("customerID", "Customer ID", "INT", order.getCustomerID()));
        fieldDefinitions.add(new FieldDefinition("employeeID", "Employee ID", "INT", order.getEmployeeID()));
        fieldDefinitions.add(new FieldDefinition("quantity", "Quantity", "INT", order.getQuantity()));
        fieldDefinitions.add(new FieldDefinition("totalPrice", "Total Price", "DOUBLE", order.getTotalPrice()));

        AppStage.getInstance().showModal(
                order,
                fieldDefinitions,
                "Edit Order",
                "Update",
                () -> {
                    try {
                        boolean success = orderService.updateOrder(order);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Order updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the order.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the order:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    // Cancel action
                });
    }
    public void handleInsert() {
        Order newOrder = new Order(0, new Date(), 0, 0, 0, 0, 0.0);

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("orderDate", "Order Date", "DATE", newOrder.getSimpleDate()),
                new FieldDefinition("carID", "Car ID", "INT", newOrder.getCarID()),
                new FieldDefinition("customerID", "Customer ID", "INT", newOrder.getCustomerID()),
                new FieldDefinition("employeeID", "Employee ID", "INT", newOrder.getEmployeeID()),
                new FieldDefinition("quantity", "Quantity", "INT", newOrder.getQuantity()),
                new FieldDefinition("totalPrice", "Total Price", "DOUBLE", newOrder.getTotalPrice()));

        AppStage.getInstance().showModal(
                newOrder,
                fieldDefinitions,
                "Add New Order",
                "Add",
                () -> {
                    try {
                        if (orderService.insertOrder(newOrder)) {
                            orderList.add(newOrder);
                            AppStage.getInstance().showMessage("Success", "New order added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new order.",
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

    public ObservableList<Order> loadAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            orderList.setAll(orders);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading orders:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredOrderList;
    }

    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = orderService.getColumnNamesAndTypes("orders");
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

    public void resetFilters() {
        filteredOrderList.setPredicate(order -> true);
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("order_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                orderService.exportOrdersToCSV(filteredOrderList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Orders have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting orders:\n" + e.getMessage(),
                        MessageDialog.MessageType.ERROR);
            }
        }
    }

    public ObservableList<Order> getOrderList() {
        return filteredOrderList;
    }

    public void filterOrders(List<CustomSearchBox> searchBoxes) {
        filteredOrderList.setPredicate(order -> {
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
                    boolean matches = applyFilter(dataType, input, order.getFieldValue(searchBox.getColumnText()));
                    if (!matches) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

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

    private boolean fuzzyMatch(String fieldValue, String input) {
        return FuzzySearch.ratio(fieldValue.toLowerCase(), input.toLowerCase()) >= 60;
    }

    private boolean safeFilterInt(String input, String fieldValue) {
        try {
            int actualValue = Integer.parseInt(fieldValue);
            return applyIntFilter(input, actualValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean safeFilterDecimal(String input, String fieldValue) {
        try {
            double actualValue = Double.parseDouble(fieldValue);
            return applyDecimalFilter(input, actualValue);
        } catch (NumberFormatException e) {
            return false;
        }
    }

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

    private boolean isDateInputValid(String input) {
        try {
            // Assuming input is in format YYYY-MM-DD
            java.time.LocalDate.parse(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void handleDetails(Order order) {
        // Implement details view for Order
        // For example, show related Car, Customer, and Employee information
        try {
            // Assuming you have DAOs or Services to fetch related data
            // For simplicity, we'll just display order details here

            showOrderDetails(order);
        } catch (Exception e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load details for order:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    /**
     * Displays the DetailsDialog with order information and related entities.
     *
     * @param order The order whose details are to be displayed.
     */
    private void showOrderDetails(Order order) {
        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Details for Order ID: " + order.getOrderID());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Order Info
        VBox orderInfoBox = new VBox(5);
        Label orderIDLabel = new Label("Order ID: " + order.getOrderID());
        Label orderDateLabel = new Label("Order Date: " + order.getSimpleDate());
        Label carIDLabel = new Label("Car ID: " + order.getCarID());
        Label customerIDLabel = new Label("Customer ID: " + order.getCustomerID());
        Label employeeIDLabel = new Label("Employee ID: " + order.getEmployeeID());
        Label quantityLabel = new Label("Quantity: " + order.getQuantity());
        Label totalPriceLabel = new Label(String.format("Total Price: $%.2f", order.getTotalPrice()));

        orderInfoBox.getChildren().addAll(orderIDLabel, orderDateLabel, carIDLabel, customerIDLabel,
                employeeIDLabel, quantityLabel, totalPriceLabel);

        // Placeholder for related entities (Car, Customer, Employee)
        // You can fetch and display related data here if you have the necessary
        // DAOs/Services

        // Example:
        // Car car = carService.getCarById(order.getCarID());
        // Customer customer = customerService.getCustomerById(order.getCustomerID());
        // Employee employee = employeeService.getEmployeeById(order.getEmployeeID());

        // For demonstration, we'll add dummy labels
        Label relatedInfoLabel = new Label("Related Information:");
        Label carInfoLabel = new Label("Car Details: [Car Details Here]");
        Label customerInfoLabel = new Label("Customer Details: [Customer Details Here]");
        Label employeeInfoLabel = new Label("Employee Details: [Employee Details Here]");

        contentBox.getChildren().addAll(orderInfoBox, relatedInfoLabel, carInfoLabel, customerInfoLabel,
                employeeInfoLabel);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }
}
