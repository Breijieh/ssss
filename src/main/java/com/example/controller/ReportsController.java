package com.example.controller;

import com.example.components.dialog.MessageDialog;
import com.example.AppStructure.AppStage;
import com.example.service.ReportService;
import com.example.components.general.CustomComboBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.*;

public class ReportsController {

    private final ReportService reportService;

    // Containers now hold tables instead of charts
    private final VBox serviceFrequencyContainer;
    private final VBox revenueContainer;

    public ReportsController() {
        this.reportService = new ReportService();
        this.serviceFrequencyContainer = new VBox();
        this.revenueContainer = new VBox();
    }

    public Node generateServiceFrequencyReport() {
        VBox reportBox = new VBox(10);
        reportBox.setPadding(new Insets(10));
        reportBox.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Frequency of Services");
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));

        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        CustomComboBox<String> modelComboBox = new CustomComboBox<>("Select Car Model:");

        try {
            List<String> models = reportService.getAllCarModels();
            List<String> allModels = new ArrayList<>();
            allModels.add("All Models");
            allModels.addAll(models);
            modelComboBox.setItems(allModels.toArray(new String[0]));
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load car models:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }

        selectionBox.getChildren().addAll(modelComboBox);

        serviceFrequencyContainer.setAlignment(Pos.CENTER);

        // Create initial table
        TableView<ServiceFrequencyData> initialTable = createServiceFrequencyTable(null);
        serviceFrequencyContainer.getChildren().add(initialTable);

        modelComboBox.setOnAction(event -> {
            String selectedModel = modelComboBox.getValue();
            String modelFilter = "All Models".equals(selectedModel) ? null : selectedModel;
            updateServiceFrequencyTable(modelFilter);
        });

        reportBox.getChildren().addAll(titleLabel, selectionBox, serviceFrequencyContainer);
        return reportBox;
    }

    private TableView<ServiceFrequencyData> createServiceFrequencyTable(String modelFilter) {
        TableView<ServiceFrequencyData> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ServiceFrequencyData, String> serviceTypeCol = new TableColumn<>("Service Type");
        serviceTypeCol.setCellValueFactory(data -> data.getValue().serviceTypeProperty());

        TableColumn<ServiceFrequencyData, Number> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setCellValueFactory(data -> data.getValue().frequencyProperty());

        table.getColumns().addAll(serviceTypeCol, frequencyCol);

        try {
            Map<String, Integer> serviceFrequency = reportService.getServiceFrequency(modelFilter);

            List<ServiceFrequencyData> dataList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : serviceFrequency.entrySet()) {
                dataList.add(new ServiceFrequencyData(entry.getKey(), entry.getValue()));
            }

            table.setItems(FXCollections.observableArrayList(dataList));

            if (dataList.isEmpty()) {
                AppStage.getInstance().showMessage("No Data",
                        "No frequency data available for the selected model.",
                        MessageDialog.MessageType.INFORMATION);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load service frequency data:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }

        return table;
    }

    private void updateServiceFrequencyTable(String modelFilter) {
        serviceFrequencyContainer.getChildren().clear();
        TableView<ServiceFrequencyData> newTable = createServiceFrequencyTable(modelFilter);
        serviceFrequencyContainer.getChildren().add(newTable);
    }

    public Node generateRevenueReport() {
        VBox reportBox = new VBox(10);
        reportBox.setPadding(new Insets(10));
        reportBox.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label("Revenue from Services");
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 16));

        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        CustomComboBox<Integer> yearComboBox = new CustomComboBox<>("Select Year:");
        CustomComboBox<String> aggregationComboBox = new CustomComboBox<>("Aggregation:");

        try {
            List<Integer> years = reportService.getDistinctServiceYears();
            yearComboBox.setItems(years.toArray(new Integer[0]));
            if (!years.isEmpty()) {
                yearComboBox.getComboBox().getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load available years:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }

        aggregationComboBox.setItems("Monthly", "Quarterly");
        aggregationComboBox.getComboBox().getSelectionModel().selectFirst();

        selectionBox.getChildren().addAll(yearComboBox, aggregationComboBox);

        revenueContainer.setAlignment(Pos.CENTER);

        Integer defaultYear = yearComboBox.getValue();
        String defaultAggregation = aggregationComboBox.getValue();
        if (defaultYear != null && defaultAggregation != null) {
            TableView<RevenueData> initialTable = createRevenueTable(defaultYear, defaultAggregation);
            revenueContainer.getChildren().add(initialTable);
        }

        yearComboBox.setOnAction(event -> {
            Integer selectedYear = yearComboBox.getValue();
            String aggregation = aggregationComboBox.getValue();
            if (selectedYear != null && aggregation != null) {
                updateRevenueTable(selectedYear, aggregation);
            }
        });

        aggregationComboBox.setOnAction(event -> {
            Integer selectedYear = yearComboBox.getValue();
            String aggregation = aggregationComboBox.getValue();
            if (selectedYear != null && aggregation != null) {
                updateRevenueTable(selectedYear, aggregation);
            }
        });

        reportBox.getChildren().addAll(titleLabel, selectionBox, revenueContainer);
        return reportBox;
    }

    private TableView<RevenueData> createRevenueTable(int year, String aggregation) {
        TableView<RevenueData> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RevenueData, String> serviceTypeCol = new TableColumn<>("Service Type");
        serviceTypeCol.setCellValueFactory(data -> data.getValue().serviceTypeProperty());

        table.getColumns().add(serviceTypeCol);

        // Map aggregation text to expected parameter
        String aggregationParam = "MONTH".equalsIgnoreCase(aggregation) ? "MONTH" : "QUARTER";

        try {
            Map<String, Map<String, Double>> revenueData = reportService.getRevenueByServiceType(year,
                    aggregationParam);

            // Determine all periods
            Set<String> periods = new TreeSet<>();
            for (Map<String, Double> periodMap : revenueData.values()) {
                periods.addAll(periodMap.keySet());
            }

            if (periods.isEmpty()) {
                AppStage.getInstance().showMessage("No Data",
                        "No revenue data available for the selected year and aggregation.",
                        MessageDialog.MessageType.INFORMATION);
                return table;
            }

            // Dynamically create columns for each period
            for (String period : periods) {
                TableColumn<RevenueData, Number> periodCol = new TableColumn<>(period);
                periodCol.setCellValueFactory(data -> {
                    Double value = data.getValue().getPeriodRevenue(period);
                    return new SimpleDoubleProperty(value != null ? value : 0.0);
                });
                table.getColumns().add(periodCol);
            }

            // Populate rows
            List<RevenueData> dataList = new ArrayList<>();
            for (Map.Entry<String, Map<String, Double>> entry : revenueData.entrySet()) {
                String serviceType = entry.getKey();
                Map<String, Double> periodMap = entry.getValue();
                dataList.add(new RevenueData(serviceType, periodMap));
            }

            table.setItems(FXCollections.observableArrayList(dataList));

        } catch (SQLException e) {
            e.printStackTrace();
            AppStage.getInstance().showMessage("Error",
                    "Failed to load revenue data:\n" + e.getMessage(),
                    MessageDialog.MessageType.ERROR);
        }

        return table;
    }

    private void updateRevenueTable(int year, String aggregation) {
        revenueContainer.getChildren().clear();
        TableView<RevenueData> newTable = createRevenueTable(year, aggregation);
        revenueContainer.getChildren().add(newTable);
    }

    public void cleanup() {
        reportService.close();
    }

    /**
     * Simple data model for service frequency table.
     */
    public static class ServiceFrequencyData {
        private final SimpleStringProperty serviceType;
        private final SimpleIntegerProperty frequency;

        public ServiceFrequencyData(String serviceType, int frequency) {
            this.serviceType = new SimpleStringProperty(serviceType);
            this.frequency = new SimpleIntegerProperty(frequency);
        }

        public SimpleStringProperty serviceTypeProperty() {
            return serviceType;
        }

        public SimpleIntegerProperty frequencyProperty() {
            return frequency;
        }
    }

    /**
     * Data model for revenue data table.
     * Each row represents a service type, with a map of period->revenue.
     */
    public static class RevenueData {
        private final SimpleStringProperty serviceType;
        private final Map<String, Double> periodRevenueMap;

        public RevenueData(String serviceType, Map<String, Double> periodRevenueMap) {
            this.serviceType = new SimpleStringProperty(serviceType);
            this.periodRevenueMap = periodRevenueMap;
        }

        public SimpleStringProperty serviceTypeProperty() {
            return serviceType;
        }

        public Double getPeriodRevenue(String period) {
            return periodRevenueMap.getOrDefault(period, 0.0);
        }
    }
}
