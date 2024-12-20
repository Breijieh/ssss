package com.example.views.Tables;

import com.example.model.Order;
import com.example.components.general.StyledTableComponent;
import com.example.controller.OrderController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class OrderTableComponent extends StyledTableComponent<Order> {
    private OrderController orderController;

    public OrderTableComponent(OrderController orderController) {
        super("Order List");
        this.orderController = orderController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Order, Number> orderIDColumn = createColumn("Order ID", "orderID", 100);
        TableColumn<Order, String> orderDateColumn = createColumn("Order Date", "orderDate", 150);
        TableColumn<Order, Number> carIDColumn = createColumn("Car ID", "carID", 80);
        TableColumn<Order, Number> customerIDColumn = createColumn("Customer ID", "customerID", 100);
        TableColumn<Order, Number> employeeIDColumn = createColumn("Employee ID", "employeeID", 100);
        TableColumn<Order, Number> quantityColumn = createColumn("Quantity", "quantity", 80);
        TableColumn<Order, Number> totalPriceColumn = createColumn("Total Price", "totalPrice", 100);

        table.getColumns().addAll(
                orderIDColumn,
                orderDateColumn,
                carIDColumn,
                customerIDColumn,
                employeeIDColumn,
                quantityColumn,
                totalPriceColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Order> orderList) {
        table.setItems(orderList);
    }

    @Override
    protected void onEdit(Order order) {
        orderController.handleEdit(order);
    }

    @Override
    protected void onDetails(Order order) {
        orderController.handleDetails(order);
    }

    @Override
    protected void onInsert() {
        orderController.handleInsert();
    }

    public void exportToCSV() {
        orderController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
