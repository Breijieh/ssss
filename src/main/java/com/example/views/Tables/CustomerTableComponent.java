package com.example.views.Tables;

import com.example.model.Customer;
import com.example.components.general.StyledTableComponent;
import com.example.controller.CustomerController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class CustomerTableComponent extends StyledTableComponent<Customer> {
    private CustomerController customerController;

    public CustomerTableComponent(CustomerController customerController) {
        super("Customer List");
        this.customerController = customerController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Customer, Number> customerIDColumn = createColumn("Customer ID", "customerID", 100);
        TableColumn<Customer, String> firstNameColumn = createColumn("First Name", "firstName", 120);
        TableColumn<Customer, String> lastNameColumn = createColumn("Last Name", "lastName", 120);
        TableColumn<Customer, String> emailColumn = createColumn("Email", "email", 200);
        TableColumn<Customer, String> phoneColumn = createColumn("Phone", "phone", 150);
        TableColumn<Customer, String> addressColumn = createColumn("Address", "address", 200);
        TableColumn<Customer, String> cityColumn = createColumn("City", "city", 100);
        TableColumn<Customer, String> stateColumn = createColumn("State", "state", 80);
        TableColumn<Customer, String> zipCodeColumn = createColumn("Zip Code", "zipCode", 100);

        table.getColumns().addAll(
                customerIDColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                phoneColumn,
                addressColumn,
                cityColumn,
                stateColumn,
                zipCodeColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Customer> customerList) {
        table.setItems(customerList);
    }

    @Override
    protected void onEdit(Customer customer) {
        customerController.handleEdit(customer);
    }

    @Override
    protected void onDetails(Customer customer) {
        customerController.handleDetails(customer);
    }

    @Override
    protected void onInsert() {
        customerController.handleInsert();
    }

    public void exportToCSV() {
        customerController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
