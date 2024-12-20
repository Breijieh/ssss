package com.example.controller;

import com.example.components.general.CustomSearchBox;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.components.dialog.DetailsDialog;
import com.example.AppStructure.AppStage;
import com.example.model.Car;
import com.example.model.CarServiceRecord;
import com.example.service.CarService;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class CarController {
    private CarService carService;
    private ObservableList<Car> carList;
    private FilteredList<Car> filteredCarList;
    private final String regex = ".*[a-zA-Z].*";

    public CarController() {
        this.carService = new CarService();
        this.carList = FXCollections.observableArrayList();
        this.filteredCarList = new FilteredList<>(carList, p -> true);
    }

    public void handleEdit(Car car) {
        List<FieldDefinition> fieldDefinitions = new ArrayList<>();
        fieldDefinitions.add(new FieldDefinition("make", "Make", "VARCHAR", car.getMake()));
        fieldDefinitions.add(new FieldDefinition("model", "Model", "VARCHAR", car.getModel()));
        fieldDefinitions.add(new FieldDefinition("year", "Year", "INT", car.getYear()));
        fieldDefinitions.add(new FieldDefinition("price", "Price", "DOUBLE", car.getPrice()));
        fieldDefinitions.add(new FieldDefinition("stock", "Stock", "INT", car.getStock()));
        fieldDefinitions.add(new FieldDefinition("vin", "VIN", "VARCHAR", car.getVin()));

        AppStage.getInstance().showModal(
                car,
                fieldDefinitions,
                "Edit Car",
                "Update",
                () -> {
                    try {
                        boolean success = carService.updateCar(car);
                        if (success) {
                            AppStage.getInstance().showMessage("Success", "Car updated successfully.",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Update Failed", "No changes were made to the car.",
                                    MessageDialog.MessageType.ERROR);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        AppStage.getInstance().showMessage("Update Failed",
                                "An error occurred while updating the car:\n" + e.getMessage(),
                                MessageDialog.MessageType.ERROR);
                    }
                },
                () -> {

                });
    }

    public void handleInsert() {
        Car newCar = new Car();

        List<FieldDefinition> fieldDefinitions = List.of(
                new FieldDefinition("make", "Make", "VARCHAR", ""),
                new FieldDefinition("model", "Model", "VARCHAR", ""),
                new FieldDefinition("year", "Year", "INT", 0),
                new FieldDefinition("price", "Price", "DOUBLE", 0.0),
                new FieldDefinition("stock", "Stock", "INT", 0),
                new FieldDefinition("vin", "VIN", "VARCHAR", ""));

        AppStage.getInstance().showModal(
                newCar,
                fieldDefinitions,
                "Add New Car",
                "Add",
                () -> {
                    try {
                        if (carService.insertCar(newCar)) {
                            carList.add(newCar);
                            AppStage.getInstance().showMessage("Success", "New car added successfully!",
                                    MessageDialog.MessageType.INFORMATION);
                        } else {
                            AppStage.getInstance().showMessage("Insertion Failed", "Unable to add new car.",
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

    public ObservableList<Car> loadAllCars() {
        try {
            List<Car> cars = carService.getAllCars();
            carList.setAll(cars);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Load Failed",
                    "An error occurred while loading cars:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
        }
        return filteredCarList;
    }

    public List<CustomSearchBox> getSearchBoxes() {
        try {
            Map<String, String> columns = carService.getColumnNamesAndTypes("cars");
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
        filteredCarList.setPredicate(car -> true);
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("car_report.csv");

        Stage stage = (Stage) AppStage.getInstance().getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                carService.exportCarsToCSV(filteredCarList, file);
                AppStage.getInstance().showMessage("Export Successful",
                        "Cars have been exported successfully to:\n" + file.getAbsolutePath(),
                        MessageDialog.MessageType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                AppStage.getInstance().showMessage("Export Failed",
                        "An error occurred while exporting cars:\n" + e.getMessage(), MessageDialog.MessageType.ERROR);
            }
        }
    }

    public ObservableList<Car> getCarList() {
        return filteredCarList;
    }

    // ----------------------------Searching---------------------------------------------
    public void filterCars(List<CustomSearchBox> searchBoxes) {
        filteredCarList.setPredicate(car -> {
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
                    boolean matches = applyFilter(dataType, input, car.getFieldValue(searchBox.getColumnText()));
                    if (!matches) {
                        return false;
                    }
                }
            }
            return true;
        });
    }

    // helper method for filterCars
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

    // helper method for filterCars
    private boolean fuzzyMatch(String fieldValue, String input) {
        return FuzzySearch.ratio(fieldValue.toLowerCase(), input.toLowerCase()) >= 60;
    }

    // validating the int
    private boolean safeFilterInt(String input, String fieldValue) {
        try {
            int actualValue = Integer.parseInt(fieldValue);
            return applyIntFilter(input, actualValue);
        } catch (NumberFormatException e) {

            return false;
        }
    }

    // validating the decimal
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
            double lowerBound = Double.parseDouble(range[0].trim());
            double upperBound = Double.parseDouble(range[1].trim());
            return actualValue >= lowerBound && actualValue <= upperBound;
        }
        double exactValue = Double.parseDouble(input.trim());
        return actualValue == exactValue;
    }

    // ---------validating the input----------------
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
    // ---------validating the input----------------
    // ----------------------------__Searching__---------------------------------------------

    public void handleDetails(Car car) {
        try {
            List<CarServiceRecord> services = carService.getServicesForCar(car.getCarID());

            showCarServicesDetails(car, services);
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load services for car:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }
    }

    // helper for handelDetails create table and show
    @SuppressWarnings("unchecked")
    private void showCarServicesDetails(Car car, List<CarServiceRecord> services) {

        DetailsDialog detailsDialog = AppStage.getInstance().getDetailsDialog();

        detailsDialog.setTitle("Services for Car: " + car.getMake() + " " + car.getModel());

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.TOP_LEFT);

        VBox carInfoBox = new VBox(5);
        Label carIDLabel = new Label("Car ID: " + car.getCarID());
        Label makeLabel = new Label("Make: " + car.getMake());
        Label modelLabel = new Label("Model: " + car.getModel());
        Label yearLabel = new Label("Year: " + car.getYear());
        Label priceLabel = new Label(String.format("Price: $%.2f", car.getPrice()));
        Label stockLabel = new Label("Stock: " + car.getStock());
        Label vinLabel = new Label("VIN: " + car.getVin());

        carInfoBox.getChildren().addAll(carIDLabel, makeLabel, modelLabel, yearLabel, priceLabel, stockLabel, vinLabel);

        TableView<CarServiceRecord> serviceTable = new TableView<>();
        serviceTable.setPrefHeight(200);
        serviceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CarServiceRecord, Integer> serviceIDCol = new TableColumn<>("Service ID");
        serviceIDCol.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<CarServiceRecord, LocalDate> serviceDateCol = new TableColumn<>("Date");
        serviceDateCol.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<CarServiceRecord, String> serviceDescCol = new TableColumn<>("Description");
        serviceDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<CarServiceRecord, Double> serviceCostCol = new TableColumn<>("Cost");
        serviceCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

        serviceTable.getColumns().addAll(serviceIDCol, serviceDateCol, serviceDescCol, serviceCostCol);

        serviceTable.getItems().addAll(services);

        double totalCost = services.stream().mapToDouble(CarServiceRecord::getCost).sum();
        Label totalCostLabel = new Label(String.format("Total Cost of Services: $%.2f", totalCost));
        totalCostLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        contentBox.getChildren().addAll(carInfoBox, serviceTable, totalCostLabel);

        detailsDialog.setCustomContent(contentBox);
        detailsDialog.show();
    }
}
