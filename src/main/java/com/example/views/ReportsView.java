package com.example.views;

import com.example.controller.ReportsController;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View component for displaying reports.
 */
public class ReportsView extends ScrollPane {

    private ReportsController reportsController;

    public ReportsView() {
        reportsController = new ReportsController();

        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(30);
        contentBox.getStyleClass().add("primary-bg");

        // Header
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 1. Service Frequency Report (Consolidated)
        Node serviceFrequencyReport = reportsController.generateServiceFrequencyReport();
        contentBox.getChildren().add(serviceFrequencyReport);

        // 2. Revenue Report
        Node revenueReport = reportsController.generateRevenueReport();
        contentBox.getChildren().add(revenueReport);

        // Set the content of the ScrollPane
        setContent(contentBox);
        setFitToWidth(true);
        setPannable(true);
        getStyleClass().add("primary-bg");
        setPadding(new Insets(20));
    }

    /**
     * Call this method to perform cleanup when ReportsView is no longer needed.
     */
    public void cleanup() {
        reportsController.cleanup();
    }
}
