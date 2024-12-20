package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.ServiceController;
import com.example.model.Service;
import com.example.views.Tables.ServiceTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ServicesView extends ScrollPane {
    private ServiceController serviceController;
    private ServiceTableComponent serviceTableComponent;
    private GeneralSearchComponent serviceSearchComponent;

    public ServicesView() {
        this.serviceController = new ServiceController();
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

        serviceTableComponent = new ServiceTableComponent(serviceController);

        // Load all services
        ObservableList<Service> serviceList = serviceController.loadAllServices();
        serviceTableComponent.setData(serviceList);

        List<CustomSearchBox> searchBoxes = serviceController.getSearchBoxes();

        Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> serviceController
                .filterServices(searchBoxesList);

        serviceSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

        export.setOnAction(event -> {
            serviceController.handleExport();
        });

        contentBox.getChildren().addAll(serviceSearchComponent, serviceTableComponent);
    }
}
