package com.example.service;

import com.example.dao.CustomerDAO;
import com.example.model.Customer;
import com.example.model.Payment;
import com.example.model.Service;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private CustomerDAO customerDAO;
    private ExportUtil exportUtil;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
        this.exportUtil = new ExportUtil();
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        return customerDAO.getColumnNamesAndTypes(tableName);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerDAO.getAllCustomers();
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        return customerDAO.updateCustomer(customer);
    }

    public boolean insertCustomer(Customer customer) throws SQLException {
        return customerDAO.insertCustomer(customer);
    }

    public boolean deleteCustomer(int customerID) throws SQLException {
        return customerDAO.deleteCustomer(customerID);
    }

    public List<Service> getServicesForCustomer(int customerID) throws SQLException {
        return customerDAO.getServicesForCustomer(customerID);
    }

    public List<Payment> getPaymentsForCustomer(int customerID) throws SQLException {
        return customerDAO.getPaymentsForCustomer(customerID);
    }

    public void exportCustomersToCSV(List<Customer> customers, File destination) throws IOException {
        exportUtil.exportCustomersToCSV(customers, destination);
    }

    public Customer getCustomerById(int customerID) throws SQLException {
        return customerDAO.getCustomerById(customerID);
    }
}
