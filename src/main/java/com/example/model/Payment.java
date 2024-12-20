package com.example.model;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Payment {
    private final IntegerProperty paymentID;
    private final ObjectProperty<Date> paymentDate;
    private final StringProperty paymentMethod;
    private final DoubleProperty amount;
    private final IntegerProperty orderID;

    public Payment(int paymentID, Date paymentDate, String paymentMethod, double amount, int orderID) {
        this.paymentID = new SimpleIntegerProperty(paymentID);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.amount = new SimpleDoubleProperty(amount);
        this.orderID = new SimpleIntegerProperty(orderID);
    }

    // Getters and Setters

    public int getPaymentID() {
        return paymentID.get();
    }

    public void setPaymentID(int paymentID) {
        this.paymentID.set(paymentID);
    }

    public IntegerProperty paymentIDProperty() {
        return paymentID;
    }

    public Date getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public ObjectProperty<Date> paymentDateProperty() {
        return paymentDate;
    }

    public String getSimpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(paymentDate.get());
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public int getOrderID() {
        return orderID.get();
    }

    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }

    public IntegerProperty orderIDProperty() {
        return orderID;
    }

    // Method to get field value by column name
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "paymentid":
                return String.valueOf(getPaymentID());
            case "paymentdate":
                return getSimpleDate().toString();
            case "paymentmethod":
                return getPaymentMethod();
            case "amount":
                return String.valueOf(getAmount());
            case "orderid":
                return String.valueOf(getOrderID());
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + getPaymentID() +
                ", paymentDate=" + getSimpleDate() +
                ", paymentMethod='" + getPaymentMethod() + '\'' +
                ", amount=" + getAmount() +
                ", orderID=" + getOrderID() +
                '}';
    }
}
