package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.DetailsDialog;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.model.Car;
import com.example.model.Customer;
import com.example.model.Service;
import com.example.service.CarService;
import com.example.service.CustomerService;
import com.example.service.ServiceService;
import com.example.AppStructure.AppStage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class ServiceController {
    private ServiceService serviceService;
    private ObservableList<Service> serviceList;
    private FilteredList<Service> filteredServiceList;
    private final String regex = ".*[a-zA-Z].*";

    public ServiceController() {
        this.serviceService = new ServiceService();
        this.serviceList = FXCollections.observableArrayList();
        this.filteredServiceList = new FilteredList<>(serviceList, p -> true);
    }

    public void handleEdit(Service service) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("carID", "Car ID", "INT", service.getCarID()));
        fieldDefinitions.add(new FieldDefinition("customerID", "Customer ID", "INT", service.getCustomerID()));
        fieldDefinitions.add(new FieldDefinition("serviceDate", "Service Date", "DATE", service.getSimpleDate()));
        fieldDefinitions.add(new FieldDefinition("serviceDescription", "Service Description", "VARCHAR",
                service.getServiceDescription()));
        fieldDefinitions.add(new FieldDefinition("cost", "Cost", "DOUBLE", service.getCost()));

        AppStage.getInstance().showModal(
                service,
                fieldDefinitions,
                "Edit Service",
                "Update",
                () -> {
                    try {
                        boolean success = serviceService.updateService(service);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Service updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the service.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the service:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {
                    // Cancel action
                });
    }

    public void handleInsert() {
        Service newService = new Service(0, 0, 0, new Date(), "", 0.0);

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("carID", "Car ID", "INT", newService.getCarID()),
                new FieldDefinition("customerID", "Customer ID", "INT", newService.getCustomerID()),
                new FieldDefinition("serviceDate", "Service Date", "DATE", newService.getSimpleDate()),
                new FieldDefinition("serviceDescription", "Service Description", "VARCHAR",
                        newService.getServiceDescription()),
                new FieldDefinition("cost", "Cost", "DOUBLE", newService.getCost()));

        AppStage.getInstance().showModal(
                newService,
                fieldDefinitions,
                "Add New Service",
                "Add",
                () -> {
                    try {
                        if (serviceService.insertService(newService)) {
                            serviceList.add(newService);
                            AppStage.getInstance().showMessage("Success", "New service added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new service.",
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

    public ObservableList<Service> loadAllServices() {
        try {
            List<Service> services = serviceService.getAllServices();
            serviceList.setAll(services);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading services:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredServiceList;
    }

    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = serviceService.getColumnNamesAndTypes("services");
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
        filteredServiceList.setPredicate(service -> true);
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("service_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                serviceService.exportServicesToCSV(filteredServiceList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Services have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting services:\n" + e.getMessage(),
                        MessageDialog.MessageType.ERROR);
            }
        }
    }

    public ObservableList<Service> getServiceList() {
        return filteredServiceList;
    }

    public void filterServices(List<CustomSearchBox> searchBoxes) {
        filteredServiceList.setPredicate(service -> {
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
                    boolean matches = applyFilter(dataType, input, service.getFieldValue(searchBox.getColumnText()));
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

    public void handleDetails(Service service) {
        try {
            // Assuming you have DAOs or Services to fetch related data
            // For example, show related Car and Customer information

            showServiceDetails(service);
        } catch (Exception e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load details for service:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    /**
     * Displays the DetailsDialog with service information and related entities.
     *
     * @param service The service whose details are to be displayed.
     */
    private void showServiceDetails(Service service) {
        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Details for Service ID: " + service.getServiceID());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        // Service Info
        VBox serviceInfoBox = new VBox(5);
        Label serviceIDLabel = new Label("Service ID: " + service.getServiceID());
        Label carIDLabel = new Label("Car ID: " + service.getCarID());
        Label customerIDLabel = new Label("Customer ID: " + service.getCustomerID());
        Label serviceDateLabel = new Label("Service Date: " + service.getSimpleDate());
        Label serviceDescLabel = new Label("Service Description: " + service.getServiceDescription());
        Label costLabel = new Label(String.format("Cost: $%.2f", service.getCost()));

        serviceInfoBox.getChildren().addAll(serviceIDLabel, carIDLabel, customerIDLabel,
                serviceDateLabel, serviceDescLabel, costLabel);

        // Related Car Info
        VBox carInfoBox = new VBox(5);
        try {
            // Assuming you have a method getCarById in CarService
            CarService carService = new CarService();
            Car car = carService.getCarById(service.getCarID());
            Label carInfoLabel = new Label("Car Details:");
            Label carMakeLabel = new Label("Make: " + car.getMake());
            Label carModelLabel = new Label("Model: " + car.getModel());
            Label carYearLabel = new Label("Year: " + car.getYear());
            Label carPriceLabel = new Label(String.format("Price: $%.2f", car.getPrice()));
            Label carStockLabel = new Label("Stock: " + car.getStock());
            Label carVinLabel = new Label("VIN: " + car.getVin());

            carInfoBox.getChildren().addAll(carInfoLabel, carMakeLabel, carModelLabel, carYearLabel, carPriceLabel,
                    carStockLabel, carVinLabel);
        } catch (SQLException e) {
            carInfoBox.getChildren().add(new Label("Car details not available."));
        }

        // Related Customer Info
        VBox customerInfoBox = new VBox(5);
        try {
            // Assuming you have a method getCustomerById in CustomerService
            CustomerService customerService = new CustomerService();
            Customer customer = customerService.getCustomerById(service.getCustomerID());
            Label customerInfoLabel = new Label("Customer Details:");
            Label customerNameLabel = new Label("Name: " + customer.getFirstName() + " " + customer.getLastName());
            Label customerEmailLabel = new Label("Email: " + customer.getEmail());
            Label customerPhoneLabel = new Label("Phone: " + customer.getPhone());

            customerInfoBox.getChildren().addAll(customerInfoLabel, customerNameLabel, customerEmailLabel,
                    customerPhoneLabel);
        } catch (SQLException e) {
            customerInfoBox.getChildren().add(new Label("Customer details not available."));
        }

        contentBox.getChildren().addAll(serviceInfoBox, new Label("Related Car Details:"), carInfoBox,
                new Label("Related Customer Details:"), customerInfoBox);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }
}
