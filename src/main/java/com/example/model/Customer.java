package com.example.model;

import javafx.beans.property.*;

public class Customer {
    private final IntegerProperty customerID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty email;
    private final StringProperty phone;
    private final StringProperty address;
    private final StringProperty city;
    private final StringProperty state;
    private final StringProperty zipCode;

    public Customer(int customerID, String firstName, String lastName, String email, String phone,
            String address, String city, String state, String zipCode) {
        this.customerID = new SimpleIntegerProperty(customerID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.city = new SimpleStringProperty(city);
        this.state = new SimpleStringProperty(state);
        this.zipCode = new SimpleStringProperty(zipCode);
    }

    // Getters and Setters

    public int getCustomerID() {
        return customerID.get();
    }

    public void setCustomerID(int customerID) {
        this.customerID.set(customerID);
    }

    public IntegerProperty customerIDProperty() {
        return customerID;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public StringProperty stateProperty() {
        return state;
    }

    public String getZipCode() {
        return zipCode.get();
    }

    public void setZipCode(String zipCode) {
        this.zipCode.set(zipCode);
    }

    public StringProperty zipCodeProperty() {
        return zipCode;
    }

    // Method to get field value by column name
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "customerid":
                return String.valueOf(getCustomerID());
            case "firstname":
                return getFirstName();
            case "lastname":
                return getLastName();
            case "email":
                return getEmail();
            case "phone":
                return getPhone();
            case "address":
                return getAddress();
            case "city":
                return getCity();
            case "state":
                return getState();
            case "zipcode":
                return getZipCode();
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + getCustomerID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", city='" + getCity() + '\'' +
                ", state='" + getState() + '\'' +
                ", zipCode='" + getZipCode() + '\'' +
                '}';
    }
}
