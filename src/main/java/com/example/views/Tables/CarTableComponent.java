package com.example.views.Tables;

import com.example.model.Car;
import com.example.components.general.StyledTableComponent;
import com.example.controller.CarController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class CarTableComponent extends StyledTableComponent<Car> {
    private CarController carController;

    public CarTableComponent(CarController carController) {
        super("Car Inventory");
        this.carController = carController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Car, Number> carIDColumn = createColumn("Car ID", "carID", 80);
        TableColumn<Car, String> makeColumn = createColumn("Make", "make", 120);
        TableColumn<Car, String> modelColumn = createColumn("Model", "model", 120);
        TableColumn<Car, Number> yearColumn = createColumn("Year", "year", 80);
        TableColumn<Car, Number> priceColumn = createColumn("Price", "price", 100);
        TableColumn<Car, Number> stockColumn = createColumn("Stock", "stock", 80);
        TableColumn<Car, String> vinColumn = createColumn("VIN", "vin", 150);

        table.getColumns().addAll(
                carIDColumn,
                makeColumn,
                modelColumn,
                yearColumn,
                priceColumn,
                stockColumn,
                vinColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Car> carList) {
        table.setItems(carList);
    }

    @Override
    protected void onEdit(Car car) {
        carController.handleEdit(car);
    }

    @Override
    protected void onDetails(Car car) {
        carController.handleDetails(car);
    }

    @Override
    protected void onInsert() {
        carController.handleInsert();
    }

    public void exportToCSV() {
        carController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
