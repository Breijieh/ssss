package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.DetailsDialog;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.model.Customer;
import com.example.model.Payment;
import com.example.model.Service;
import com.example.service.CustomerService;
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

public class CustomerController {
    private CustomerService customerService;
    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredCustomerList;
    private final String regex = ".*[a-zA-Z].*";

    public CustomerController() {
        this.customerService = new CustomerService();
        this.customerList = FXCollections.observableArrayList();
        this.filteredCustomerList = new FilteredList<>(customerList, p -> true);
    }

    public void handleEdit(Customer customer) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("firstName", "First Name", "VARCHAR", customer.getFirstName()));
        fieldDefinitions.add(new FieldDefinition("lastName", "Last Name", "VARCHAR", customer.getLastName()));
        fieldDefinitions.add(new FieldDefinition("email", "Email", "VARCHAR", customer.getEmail()));
        fieldDefinitions.add(new FieldDefinition("phone", "Phone", "VARCHAR", customer.getPhone()));
        fieldDefinitions.add(new FieldDefinition("address", "Address", "VARCHAR", customer.getAddress()));
        fieldDefinitions.add(new FieldDefinition("city", "City", "VARCHAR", customer.getCity()));
        fieldDefinitions.add(new FieldDefinition("state", "State", "VARCHAR", customer.getState()));
        fieldDefinitions.add(new FieldDefinition("zipCode", "Zip Code", "VARCHAR", customer.getZipCode()));

        AppStage.getInstance().showModal(
                customer,
                fieldDefinitions,
                "Edit Customer",
                "Update",
                () -> {
                    try {
                        boolean success = customerService.updateCustomer(customer);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Customer updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the customer.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the customer:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    // Cancel action
                });
    }

    public void handleInsert() {
        Customer newCustomer = new Customer(0, "", "", "", "", "", "", "", "");

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("firstName", "First Name", "VARCHAR", ""),
                new FieldDefinition("lastName", "Last Name", "VARCHAR", ""),
                new FieldDefinition("email", "Email", "VARCHAR", ""),
                new FieldDefinition("phone", "Phone", "VARCHAR", ""),
                new FieldDefinition("address", "Address", "VARCHAR", ""),
                new FieldDefinition("city", "City", "VARCHAR", ""),
                new FieldDefinition("state", "State", "VARCHAR", ""),
                new FieldDefinition("zipCode", "Zip Code", "VARCHAR", ""));

        AppStage.getInstance().showModal(
                newCustomer,
                fieldDefinitions,
                "Add New Customer",
                "Add",
                () -> {
                    try {
                        if (customerService.insertCustomer(newCustomer)) {
                            customerList.add(newCustomer);
                            AppStage.getInstance().showMessage("Success", "New customer added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new customer.",
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

    public ObservableList<Customer> loadAllCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            customerList.setAll(customers);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading customers:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredCustomerList;
    }

    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = customerService.getColumnNamesAndTypes("customers");
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
        filteredCustomerList.setPredicate(customer -> true);
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("customer_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                customerService.exportCustomersToCSV(filteredCustomerList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Customers have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting customers:\n" + e.getMessage(),
                        MessageDialog.MessageType.ERROR);
            }
        }
    }

    public ObservableList<Customer> getCustomerList() {
        return filteredCustomerList;
    }

    public void filterCustomers(List<CustomSearchBox> searchBoxes) {
        filteredCustomerList.setPredicate(customer -> {
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
                    boolean matches = applyFilter(dataType, input, customer.getFieldValue(searchBox.getColumnText()));
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

    public void handleDetails(Customer customer) {
        try {
            List<Service> services = customerService.getServicesForCustomer(customer.getCustomerID());
            List<Payment> payments = customerService.getPaymentsForCustomer(customer.getCustomerID());

            showCustomerDetails(customer, services, payments);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load details for customer:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    /**
     * Displays the DetailsDialog with customer information, services performed, and
     * payment records.
     *
     * @param customer The customer whose details are to be displayed.
     * @param services The list of service records for the customer.
     * @param payments The list of payment records for the customer.
     */
    private void showCustomerDetails(Customer customer, List<Service> services, List<Payment> payments) {

        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Details for Customer: " + customer.getFirstName() + " " + customer.getLastName());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Customer Info
        VBox customerInfoBox = new VBox(5);
        Label customerIDLabel = new Label("Customer ID: " + customer.getCustomerID());
        Label firstNameLabel = new Label("First Name: " + customer.getFirstName());
        Label lastNameLabel = new Label("Last Name: " + customer.getLastName());
        Label emailLabel = new Label("Email: " + customer.getEmail());
        Label phoneLabel = new Label("Phone: " + customer.getPhone());
        Label addressLabel = new Label("Address: " + customer.getAddress());
        Label cityLabel = new Label("City: " + customer.getCity());
        Label stateLabel = new Label("State: " + customer.getState());
        Label zipCodeLabel = new Label("Zip Code: " + customer.getZipCode());

        customerInfoBox.getChildren().addAll(customerIDLabel, firstNameLabel, lastNameLabel, emailLabel,
                phoneLabel, addressLabel, cityLabel, stateLabel, zipCodeLabel);

        // Services Table
        TableView<Service> servicesTable = new TableView<>();
        servicesTable.setPrefHeight(200);
        servicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Service, Integer> serviceIDCol = new TableColumn<>("Service ID");
        serviceIDCol.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<Service, Integer> carIDCol = new TableColumn<>("Car ID");
        carIDCol.setCellValueFactory(new PropertyValueFactory<>("carID"));

        TableColumn<Service, Date> serviceDateCol = new TableColumn<>("Date");
        serviceDateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<Service, String> serviceDescCol = new TableColumn<>("Description");
        serviceDescCol.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<Service, Double> costCol = new TableColumn<>("Cost");
        costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        servicesTable.getColumns().addAll(serviceIDCol, carIDCol, serviceDateCol, serviceDescCol, costCol);
        servicesTable.getItems().addAll(services);

        // Payments Table
        TableView<Payment> paymentsTable = new TableView<>();
        paymentsTable.setPrefHeight(200);
        paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Payment, Integer> paymentIDCol = new TableColumn<>("Payment ID");
        paymentIDCol.setCellValueFactory(new PropertyValueFactory<>("paymentID"));

        TableColumn<Payment, Date> paymentDateCol = new TableColumn<>("Date");
        paymentDateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        TableColumn<Payment, String> paymentMethodCol = new TableColumn<>("Method");
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<Payment, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        paymentsTable.getColumns().addAll(paymentIDCol, paymentDateCol, paymentMethodCol, amountCol);
        paymentsTable.getItems().addAll(payments);

        // Total Payments
        double totalPayments = payments.stream().mapToDouble(Payment::getAmount).sum();
        Label totalPaymentsLabel = new Label(String.format("Total Payments: $%.2f", totalPayments));
        totalPaymentsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        contentBox.getChildren().addAll(customerInfoBox,
                new Label("Services Performed:"), servicesTable,
                new Label("Payment Records:"), paymentsTable, totalPaymentsLabel);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }
}
