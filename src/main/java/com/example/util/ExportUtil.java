package com.example.util;

import com.example.model.*;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportUtil {

    // Export Cars
    public void exportCarsToCSV(List<Car> cars, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Car ID", "Make", "Model", "Year", "Price", "Stock", "VIN" };
            writer.writeNext(header);

            for (Car car : cars) {
                String[] data = {
                        String.valueOf(car.getCarID()),
                        car.getMake(),
                        car.getModel(),
                        String.valueOf(car.getYear()),
                        String.valueOf(car.getPrice()),
                        String.valueOf(car.getStock()),
                        car.getVin()
                };
                writer.writeNext(data);
            }
        }
    }

    // Export Customers
    public void exportCustomersToCSV(List<Customer> customers, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Customer ID", "First Name", "Last Name", "Email", "Phone", "Address", "City", "State",
                    "Zip Code" };
            writer.writeNext(header);

            for (Customer customer : customers) {
                String[] data = {
                        String.valueOf(customer.getCustomerID()),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getAddress(),
                        customer.getCity(),
                        customer.getState(),
                        customer.getZipCode()
                };
                writer.writeNext(data);
            }
        }
    }

    // Export Employees
    public void exportEmployeesToCSV(List<Employee> employees, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Employee ID", "First Name", "Last Name", "Position", "Salary", "Hire Date" };
            writer.writeNext(header);

            for (Employee employee : employees) {
                String[] data = {
                        String.valueOf(employee.getEmployeeID()),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getPosition(),
                        String.valueOf(employee.getSalary()),
                        String.valueOf(employee.getSimpleDate())
                };
                writer.writeNext(data);
            }
        }
    }

    // Export Orders
    public void exportOrdersToCSV(List<Order> orders, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Order ID", "Order Date", "Car ID", "Customer ID", "Employee ID", "Quantity",
                    "Total Price" };
            writer.writeNext(header);

            for (Order order : orders) {
                String[] data = {
                        String.valueOf(order.getOrderID()),
                        String.valueOf(order.getSimpleDate()),
                        String.valueOf(order.getCarID()),
                        String.valueOf(order.getCustomerID()),
                        String.valueOf(order.getEmployeeID()),
                        String.valueOf(order.getQuantity()),
                        String.valueOf(order.getTotalPrice())
                };
                writer.writeNext(data);
            }
        }
    }

    // Export Payments
    public void exportPaymentsToCSV(List<Payment> payments, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Payment ID", "Payment Date", "Payment Method", "Amount", "Order ID" };
            writer.writeNext(header);

            for (Payment payment : payments) {
                String[] data = {
                        String.valueOf(payment.getPaymentID()),
                        String.valueOf(payment.getSimpleDate()),
                        payment.getPaymentMethod(),
                        String.valueOf(payment.getAmount()),
                        String.valueOf(payment.getOrderID())
                };
                writer.writeNext(data);
            }
        }
    }

    // Export Services
    public void exportServicesToCSV(List<Service> services, File destination) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination))) {
            String[] header = { "Service ID", "Car ID", "Customer ID", "Service Date", "Service Description", "Cost" };
            writer.writeNext(header);

            for (Service service : services) {
                String[] data = {
                        String.valueOf(service.getServiceID()),
                        String.valueOf(service.getCarID()),
                        String.valueOf(service.getCustomerID()),
                        String.valueOf(service.getServiceDate()),
                        service.getServiceDescription(),
                        String.valueOf(service.getCost())
                };
                writer.writeNext(data);
            }
        }
    }
}
