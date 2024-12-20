package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.DetailsDialog;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.model.Payment;
import com.example.service.OrderService;
import com.example.service.PaymentService;
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

public class PaymentController {
    private PaymentService paymentService;
    private ObservableList<Payment> paymentList;
    private FilteredList<Payment> filteredPaymentList;
    private final String regex = ".*[a-zA-Z].*";

    public PaymentController() {
        this.paymentService = new PaymentService();
        this.paymentList = FXCollections.observableArrayList();
        this.filteredPaymentList = new FilteredList<>(paymentList, p -> true);
    }

    public void handleEdit(Payment payment) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("paymentDate", "Payment Date", "DATE", payment.getSimpleDate()));
        fieldDefinitions
                .add(new FieldDefinition("paymentMethod", "Payment Method", "VARCHAR", payment.getPaymentMethod()));
        fieldDefinitions.add(new FieldDefinition("amount", "Amount", "DOUBLE", payment.getAmount()));
        fieldDefinitions.add(new FieldDefinition("orderID", "Order ID", "INT", payment.getOrderID()));

        AppStage.getInstance().showModal(
                payment,
                fieldDefinitions,
                "Edit Payment",
                "Update",
                () -> {
                    try {
                        boolean success = paymentService.updatePayment(payment);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Payment updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the payment.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the payment:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    // Cancel action
                });
    }

    public void handleInsert() {
        Payment newPayment = new Payment(0, new Date(), "", 0.0, 0);

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("paymentDate", "Payment Date", "DATE", newPayment.getSimpleDate()),
                new FieldDefinition("paymentMethod", "Payment Method", "VARCHAR", newPayment.getPaymentMethod()),
                new FieldDefinition("amount", "Amount", "DOUBLE", newPayment.getAmount()),
                new FieldDefinition("orderID", "Order ID", "INT", newPayment.getOrderID()));

        AppStage.getInstance().showModal(
                newPayment,
                fieldDefinitions,
                "Add New Payment",
                "Add",
                () -> {
                    try {
                        if (paymentService.insertPayment(newPayment)) {
                            paymentList.add(newPayment);
                            AppStage.getInstance().showMessage("Success", "New payment added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new payment.",
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

    public ObservableList<Payment> loadAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            paymentList.setAll(payments);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading payments:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredPaymentList;
    }

    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = paymentService.getColumnNamesAndTypes("payments");
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
        filteredPaymentList.setPredicate(payment -> true);
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("payment_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                paymentService.exportPaymentsToCSV(filteredPaymentList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Payments have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting payments:\n" + e.getMessage(),
                        MessageDialog.MessageType.ERROR);
            }
        }
    }

    public ObservableList<Payment> getPaymentList() {
        return filteredPaymentList;
    }

    public void filterPayments(List<CustomSearchBox> searchBoxes) {
        filteredPaymentList.setPredicate(payment -> {
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
                    boolean matches = applyFilter(dataType, input, payment.getFieldValue(searchBox.getColumnText()));
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

    public void handleDetails(Payment payment) {
        // Implement details view for Payment
        // For example, show related Order information
        try {
            // Assuming you have DAOs or Services to fetch related data
            // For simplicity, we'll just display payment details here

            showPaymentDetails(payment);
        } catch (Exception e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load details for payment:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    /**
     * Displays the DetailsDialog with payment information and related order
     * details.
     *
     * @param payment The payment whose details are to be displayed.
     */
    private void showPaymentDetails(Payment payment) {
        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Details for Payment ID: " + payment.getPaymentID());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Payment Info
        VBox paymentInfoBox = new VBox(5);
        Label paymentIDLabel = new Label("Payment ID: " + payment.getPaymentID());
        Label paymentDateLabel = new Label("Payment Date: " + payment.getSimpleDate());
        Label paymentMethodLabel = new Label("Payment Method: " + payment.getPaymentMethod());
        Label amountLabel = new Label(String.format("Amount: $%.2f", payment.getAmount()));
        Label orderIDLabel = new Label("Order ID: " + payment.getOrderID());

        paymentInfoBox.getChildren().addAll(paymentIDLabel, paymentDateLabel, paymentMethodLabel, amountLabel,
                orderIDLabel);

        // Related Order Info
        VBox orderInfoBox = new VBox(5);
        try {
            // Assuming you have a method getOrderById in OrderService
            OrderService orderService = new OrderService();
            com.example.model.Order order = orderService.getOrderById(payment.getOrderID());

            Label orderIDRelatedLabel = new Label("Order ID: " + order.getOrderID());
            Label orderDateLabel = new Label("Order Date: " + order.getSimpleDate());
            Label carIDLabel = new Label("Car ID: " + order.getCarID());
            Label customerIDLabel = new Label("Customer ID: " + order.getCustomerID());
            Label employeeIDLabel = new Label("Employee ID: " + order.getEmployeeID());
            Label quantityLabel = new Label("Quantity: " + order.getQuantity());
            Label totalPriceLabel = new Label(String.format("Total Price: $%.2f", order.getTotalPrice()));

            orderInfoBox.getChildren().addAll(orderIDRelatedLabel, orderDateLabel, carIDLabel,
                    customerIDLabel, employeeIDLabel, quantityLabel, totalPriceLabel);
        } catch (SQLException e) {
            orderInfoBox.getChildren().add(new Label("Order details not available."));
        }

        contentBox.getChildren().addAll(paymentInfoBox, new Label("Related Order Details:"), orderInfoBox);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }
}
