package com.example.model.home;

import java.time.LocalDate;

public class LatestOrder {
    private String carName;
    private String customerName;
    private LocalDate orderDate;
    private double totalPrice;
    private String imageName;

    public LatestOrder(String carName, String customerName, LocalDate orderDate, double totalPrice) {
        this.carName = carName;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;

        // Assign a random image here, once.
        int randomNumber = (int) (Math.random() * 4) + 1;
        this.imageName = "car" + randomNumber + "-l.png";
    }

    public String getCarName() {
        return carName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getImageName() {
        return imageName;
    }
}
