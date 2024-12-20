package com.example.views.Tables;

import com.example.model.Payment;
import com.example.components.general.StyledTableComponent;
import com.example.controller.PaymentController;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

public class PaymentTableComponent extends StyledTableComponent<Payment> {
    private PaymentController paymentController;

    public PaymentTableComponent(PaymentController paymentController) {
        super("Payment List");
        this.paymentController = paymentController;
        initializeColumns();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initializeColumns() {
        TableColumn<Payment, Number> paymentIDColumn = createColumn("Payment ID", "paymentID", 100);
        TableColumn<Payment, String> paymentDateColumn = createColumn("Payment Date", "paymentDate", 150);
        TableColumn<Payment, String> paymentMethodColumn = createColumn("Payment Method", "paymentMethod", 150);
        TableColumn<Payment, Number> amountColumn = createColumn("Amount", "amount", 100);
        TableColumn<Payment, Number> orderIDColumn = createColumn("Order ID", "orderID", 100);

        table.getColumns().addAll(
                paymentIDColumn,
                paymentDateColumn,
                paymentMethodColumn,
                amountColumn,
                orderIDColumn,
                createActionColumn());
    }

    public void setData(ObservableList<Payment> paymentList) {
        table.setItems(paymentList);
    }

    @Override
    protected void onEdit(Payment payment) {
        paymentController.handleEdit(payment);
    }

    @Override
    protected void onDetails(Payment payment) {
        paymentController.handleDetails(payment);
    }

    @Override
    protected void onInsert() {
        paymentController.handleInsert();
    }

    public void exportToCSV() {
        paymentController.handleExport();
    }

    public void refreshTable() {
        table.refresh();
    }
}
