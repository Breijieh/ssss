package com.example.model;

import javafx.beans.property.*;

public class Car {
    private final IntegerProperty carID;
    private final StringProperty make;
    private final StringProperty model;
    private final IntegerProperty year;
    private final DoubleProperty price;
    private final IntegerProperty stock;
    private final StringProperty vin;

    public Car() {
        this(0, null, null, 0, 0, 0, null);
    }

    public Car(int carID, String make, String model, int year, double price, int stock, String vin) {
        this.carID = new SimpleIntegerProperty(carID);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.year = new SimpleIntegerProperty(year);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.vin = new SimpleStringProperty(vin);
    }

    public int getCarID() {
        return carID.get();
    }

    public void setCarID(int value) {
        carID.set(value);
    }

    public IntegerProperty carIDProperty() {
        return carID;
    }

    public String getMake() {
        return make.get();
    }

    public void setMake(String value) {
        make.set(value);
    }

    public StringProperty makeProperty() {
        return make;
    }

    public String getModel() {
        return model.get();
    }

    public void setModel(String value) {
        model.set(value);
    }

    public StringProperty modelProperty() {
        return model;
    }

    public int getYear() {
        return year.get();
    }

    public void setYear(int value) {
        year.set(value);
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double value) {
        price.set(value);
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int value) {
        stock.set(value);
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public String getVin() {
        return vin.get();
    }

    public void setVin(String value) {
        vin.set(value);
    }

    public StringProperty vinProperty() {
        return vin;
    }

    /*
     * returns the value of a specified field as a string based on the column name
     * used in filtering in searches and in export
     * if i search in make searchbox for "ford", the method should return the value
     * of the make property
     */
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "carid":
                return String.valueOf(getCarID());
            case "make":
                return getMake();
            case "model":
                return getModel();
            case "year":
                return String.valueOf(getYear());
            case "price":
                return String.valueOf(getPrice());
            case "stock":
                return String.valueOf(getStock());
            case "vin":
                return getVin();
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Car{" +
                "carID=" + getCarID() +
                ", make='" + getMake() + '\'' +
                ", model='" + getModel() + '\'' +
                ", year=" + getYear() +
                ", price=" + getPrice() +
                ", stock=" + getStock() +
                ", vin='" + getVin() + '\'' +
                '}';
    }
}
