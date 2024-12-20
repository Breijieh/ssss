package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.CustomerController;
import com.example.model.Customer;
import com.example.views.Tables.CustomerTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CustomersView extends ScrollPane {
    private CustomerController customerController;
    private CustomerTableComponent customerTableComponent;
    private GeneralSearchComponent customerSearchComponent;

    public CustomersView() {
        this.customerController = new CustomerController();
        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(20);
        contentBox.getStyleClass().add("primary-bg");
        CustomButton export = new CustomButton("Export", "export.png");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        setContent(contentBox);
        setFitToWidth(true);
        setPannable(true);
        getStyleClass().add("primary-bg");
        setPadding(new Insets(20));

        customerTableComponent = new CustomerTableComponent(customerController);

        // Load all customers
        ObservableList<Customer> customerList = customerController.loadAllCustomers();
        customerTableComponent.setData(customerList);

        List<CustomSearchBox> searchBoxes = customerController.getSearchBoxes();

        Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> customerController
                .filterCustomers(searchBoxesList);

        customerSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

        export.setOnAction(event -> {
            customerController.handleExport();
        });

        contentBox.getChildren().addAll(customerSearchComponent, customerTableComponent);
    }
}
