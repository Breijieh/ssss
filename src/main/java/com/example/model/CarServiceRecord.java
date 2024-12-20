package com.example.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class CarServiceRecord {
    private final IntegerProperty serviceID;
    private final ObjectProperty<LocalDate> serviceDate;
    private final StringProperty description;
    private final DoubleProperty cost;

    public CarServiceRecord(int serviceID, LocalDate serviceDate, String description, double cost) {
        this.serviceID = new SimpleIntegerProperty(serviceID);
        this.serviceDate = new SimpleObjectProperty<>(serviceDate);
        this.description = new SimpleStringProperty(description);
        this.cost = new SimpleDoubleProperty(cost);
    }

    public int getServiceID() {
        return serviceID.get();
    }

    public IntegerProperty serviceIDProperty() {
        return serviceID;
    }

    public LocalDate getServiceDate() {
        return serviceDate.get();
    }

    public ObjectProperty<LocalDate> serviceDateProperty() {
        return serviceDate;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public double getCost() {
        return cost.get();
    }

    public DoubleProperty costProperty() {
        return cost;
    }

    @Override
    public String toString() {
        return "CarServiceRecord{" +
                "serviceID=" + getServiceID() +
                ", serviceDate=" + getServiceDate() +
                ", description='" + getDescription() + '\'' +
                ", cost=" + getCost() +
                '}';
    }
}
