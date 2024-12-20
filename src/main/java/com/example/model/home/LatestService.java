package com.example.model.home;

import java.time.LocalDate;

public class LatestService {
    private String carName;
    private String customerName;
    private double cost;
    private String serviceDescription;
    private LocalDate serviceDate;
    private final String imageName;

    public LatestService(String carName, String customerName, double cost, String serviceDescription,
            LocalDate serviceDate) {
        this.carName = carName;
        this.customerName = customerName;
        this.cost = cost;
        this.serviceDescription = serviceDescription;
        this.serviceDate = serviceDate;

        // Assign a random image once.
        int randomNumber = (int) (Math.random() * 4) + 1;
        this.imageName = "car" + randomNumber + "-small.png";
    }

    public String getCarName() {
        return carName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getCost() {
        return cost;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public String getImageName() {
        return imageName;
    }
}
