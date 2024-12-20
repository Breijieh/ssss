package com.example.model;

import javafx.beans.property.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Service {
    private final IntegerProperty serviceID;
    private final IntegerProperty carID;
    private final IntegerProperty customerID;
    private final ObjectProperty<Date> serviceDate;
    private final StringProperty serviceDescription;
    private final DoubleProperty cost;

    public Service(int serviceID, int carID, int customerID, Date serviceDate, String serviceDescription, double cost) {
        this.serviceID = new SimpleIntegerProperty(serviceID);
        this.carID = new SimpleIntegerProperty(carID);
        this.customerID = new SimpleIntegerProperty(customerID);
        this.serviceDate = new SimpleObjectProperty<>(serviceDate);
        this.serviceDescription = new SimpleStringProperty(serviceDescription);
        this.cost = new SimpleDoubleProperty(cost);
    }

    // Getters and Setters

    public int getServiceID() {
        return serviceID.get();
    }

    public void setServiceID(int serviceID) {
        this.serviceID.set(serviceID);
    }

    public IntegerProperty serviceIDProperty() {
        return serviceID;
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

    public Date getServiceDate() {
        return serviceDate.get();
    }

    public String getSimpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(serviceDate.get());
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate.set(serviceDate);
    }

    public ObjectProperty<Date> serviceDateProperty() {
        return serviceDate;
    }

    public String getServiceDescription() {
        return serviceDescription.get();
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription.set(serviceDescription);
    }

    public StringProperty serviceDescriptionProperty() {
        return serviceDescription;
    }

    public double getCost() {
        return cost.get();
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public DoubleProperty costProperty() {
        return cost;
    }

    // Method to get field value by column name
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "serviceid":
                return String.valueOf(getServiceID());
            case "carid":
                return String.valueOf(getCarID());
            case "customerid":
                return String.valueOf(getCustomerID());
            case "servicedate":
                return getServiceDate().toString();
            case "servicedescription":
                return getServiceDescription();
            case "cost":
                return String.valueOf(getCost());
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceID=" + getServiceID() +
                ", carID=" + getCarID() +
                ", customerID=" + getCustomerID() +
                ", serviceDate=" + getServiceDate() +
                ", serviceDescription='" + getServiceDescription() + '\'' +
                ", cost=" + getCost() +
                '}';
    }
}
