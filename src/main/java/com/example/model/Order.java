package com.example.model;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private final IntegerProperty orderID;
    private final ObjectProperty<Date> orderDate;
    private final IntegerProperty carID;
    private final IntegerProperty customerID;
    private final IntegerProperty employeeID;
    private final IntegerProperty quantity;
    private final DoubleProperty totalPrice;

    public Order(int orderID, Date orderDate, int carID, int customerID, int employeeID, int quantity,
            double totalPrice) {
        this.orderID = new SimpleIntegerProperty(orderID);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.carID = new SimpleIntegerProperty(carID);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.employeeID = new SimpleIntegerProperty(employeeID);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
    }

    // Getters and Setters

    public int getOrderID() {
        return orderID.get();
    }

    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }

    public IntegerProperty orderIDProperty() {
        return orderID;
    }

    public Date getOrderDate() {
        return orderDate.get();
    }

    public String getSimpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(orderDate.get());
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate.set(orderDate);
    }

    public ObjectProperty<Date> orderDateProperty() {
        return orderDate;
    }

    public int getCarID() {
        return carID.get();
    }

    public void setCarID(int carID) {
        this.carID.set(carID);
    }

    public IntegerProperty carIDProperty() {
        return carID;
    }

    public int getCustomerID() {
        return customerID.get();
    }

    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public IntegerProperty customerIDProperty() {
        return customerID;
    }

    public int getEmployeeID() {
        return employeeID.get();
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID.set(employeeID);
    }

    public IntegerProperty employeeIDProperty() {
        return employeeID;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public DoubleProperty totalPriceProperty() {
        return totalPrice;
    }

    // Method to get field value by column name
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "orderid":
                return String.valueOf(getOrderID());
            case "orderdate":
                return getSimpleDate();
            case "carid":
                return String.valueOf(getCarID());
            case "customerid":
                return String.valueOf(getCustomerID());
            case "employeeid":
                return String.valueOf(getEmployeeID());
            case "quantity":
                return String.valueOf(getQuantity());
            case "totalprice":
                return String.valueOf(getTotalPrice());
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + getOrderID() +
                ", orderDate=" + getSimpleDate() +
                ", carID=" + getCarID() +
                ", customerID=" + getCustomerID() +
                ", employeeID=" + getEmployeeID() +
                ", quantity=" + getQuantity() +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
