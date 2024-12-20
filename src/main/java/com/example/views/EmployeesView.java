package com.example.views;

import java.util.List;
import java.util.function.Consumer;

import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;
import com.example.components.general.GeneralSearchComponent;
import com.example.controller.EmployeeController;
import com.example.model.Employee;
import com.example.views.Tables.EmployeeTableComponent;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EmployeesView extends ScrollPane {
    private EmployeeController employeeController;
    private EmployeeTableComponent employeeTableComponent;
    private GeneralSearchComponent employeeSearchComponent;

    public EmployeesView() {
        this.employeeController = new EmployeeController();
        VBox contentBox = new VBox();
        contentBox.setPadding(new Insets(20));
        contentBox.setSpacing(20);
        contentBox.getStyleClass().add("primary-bg"); // Ensure you have a CSS class named 'primary-bg'
        CustomButton export = new CustomButton("Export", "export.png"); // Ensure 'export.png' exists in your resources
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        setContent(contentBox);
        setFitToWidth(true);
        setPannable(true);
        getStyleClass().add("primary-bg");
        setPadding(new Insets(20));

        employeeTableComponent = new EmployeeTableComponent(employeeController);

        // Load all employees
        ObservableList<Employee> employeeList = employeeController.loadAllEmployees();
        employeeTableComponent.setData(employeeList);

        List<CustomSearchBox> searchBoxes = employeeController.getSearchBoxes();

        Consumer<List<CustomSearchBox>> filterAction = searchBoxesList -> employeeController
                .filterEmployees(searchBoxesList);

        employeeSearchComponent = new GeneralSearchComponent(searchBoxes, filterAction);

        export.setOnAction(event -> {
            employeeController.handleExport();
        });

        contentBox.getChildren().addAll(employeeSearchComponent, employeeTableComponent);
    }
}
