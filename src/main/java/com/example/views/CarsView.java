package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.CarController;
import com.example.model.Car;
import com.example.views.Tables.CarTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CarsView extends ScrollPane {
        private CarController carController;
        private CarTableComponent carTableComponent;
        private GeneralSearchComponent carSearchComponent;

        public CarsView() {
                this.carController = new CarController();
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

                carTableComponent = new CarTableComponent(carController);

                // Load all cars
                ObservableList<Car> carList = carController.loadAllCars();
                carTableComponent.setData(carList);

                List<CustomSearchBox> searchBoxes = carController.getSearchBoxes();

                Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> carController
                                .filterCars(searchBoxesList);

                carSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

                export.setOnAction(event -> {
                        carController.handleExport();
                });
                contentBox.getChildren().addAll(carSearchComponent, carTableComponent);
        }
}
