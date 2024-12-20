package com.example.model.home;

public class HomeStats {
    private int totalCars;
    private int totalCustomers;
    private int totalOrders;
    private int totalEmployees;

    public HomeStats(int totalCars, int totalCustomers, int totalOrders, int totalEmployees) {
        this.totalCars = totalCars;
        this.totalCustomers = totalCustomers;
        this.totalOrders = totalOrders;
        this.totalEmployees = totalEmployees;
    }

    public int getTotalCars() {
        return totalCars;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }
}
