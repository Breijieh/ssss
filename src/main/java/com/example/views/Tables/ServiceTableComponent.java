package com.example.views.Tables;

import com.example.model.Service;
import com.example.components.general.StyledTableComponent;
import com.example.controller.ServiceController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class ServiceTableComponent extends StyledTableComponent<Service> {
    private ServiceController serviceController;

    public ServiceTableComponent(ServiceController serviceController) {
        super("Service List");
        this.serviceController = serviceController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Service, Number> serviceIDColumn = createColumn("Service ID", "serviceID", 100);
        TableColumn<Service, Number> carIDColumn = createColumn("Car ID", "carID", 80);
        TableColumn<Service, Number> customerIDColumn = createColumn("Customer ID", "customerID", 100);
        TableColumn<Service, String> serviceDateColumn = createColumn("Service Date", "serviceDate", 150);
        TableColumn<Service, String> serviceDescColumn = createColumn("Description", "serviceDescription", 200);
        TableColumn<Service, Number> costColumn = createColumn("Cost", "cost", 100);

        table.getColumns().addAll(
                serviceIDColumn,
                carIDColumn,
                customerIDColumn,
                serviceDateColumn,
                serviceDescColumn,
                costColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Service> serviceList) {
        table.setItems(serviceList);
    }

    @Override
    protected void onEdit(Service service) {
        serviceController.handleEdit(service);
    }

    @Override
    protected void onDetails(Service service) {
        serviceController.handleDetails(service);
    }

    @Override
    protected void onInsert() {
        serviceController.handleInsert();
    }

    public void exportToCSV() {
        serviceController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
