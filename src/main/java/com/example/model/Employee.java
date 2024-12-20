package com.example.model;

import javafx.beans.property.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import com.example.util.DateUtil;

public class Employee {
    private final IntegerProperty employeeID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty position;
    private final DoubleProperty salary;
    private final ObjectProperty<Date> hireDate;

    public Employee(int employeeID, String firstName, String lastName, String position, double salary, Date hireDate) {
        this.employeeID = new SimpleIntegerProperty(employeeID);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.position = new SimpleStringProperty(position);
        this.salary = new SimpleDoubleProperty(salary);
        this.hireDate = new SimpleObjectProperty<>(hireDate);
    }

    // Getters and Setters

    public int getEmployeeID() {
        return employeeID.get();
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID.set(employeeID);
    }

    public IntegerProperty employeeIDProperty() {
        return employeeID;
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

    public String getPosition() {
        return position.get();
    }

    public void setPosition(String position) {
        this.position.set(position);
    }

    public StringProperty positionProperty() {
        return position;
    }

    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    public DoubleProperty salaryProperty() {
        return salary;
    }

    public Date getHireDate() {
        return hireDate.get();
    }

    public void setHireDate(Date hireDate) {
        this.hireDate.set(hireDate);
    }

    public ObjectProperty<Date> hireDateProperty() {
        return hireDate;
    }

    /**
     * Retrieves the hire date as a formatted String.
     *
     * @return The hire date in "dd-MM-yyyy" format, or an empty string if hireDate
     *         is null.
     */
    public String getHireDateString() {
        if (getHireDate() == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Default format
        return sdf.format(getHireDate());
    }

    /**
     * Sets the hire date from a String, parsing it using multiple date formats.
     *
     * @param dateStr The date string entered by the user.
     * @throws ParseException If the date string doesn't match any accepted formats.
     */
    public void setHireDateFromString(String dateStr) throws ParseException {
        Date parsedDate = DateUtil.parseDateFlexible(dateStr);
        setHireDate(parsedDate);
    }

    public String getSimpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(hireDate.get());
    }

    // Method to get field value by column name
    public String getFieldValue(String columnName) {
        switch (columnName.toLowerCase()) {
            case "employeeid":
                return String.valueOf(getEmployeeID());
            case "firstname":
                return getFirstName();
            case "lastname":
                return getLastName();
            case "position":
                return getPosition();
            case "salary":
                return String.valueOf(getSalary());
            case "hiredate":
                return getHireDateString();
            default:
                throw new IllegalArgumentException("Unknown column: " + columnName);
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID=" + getEmployeeID() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", position='" + getPosition() + '\'' +
                ", salary=" + getSalary() +
                ", hireDate=" + getHireDateString() +
                '}';
    }
}
