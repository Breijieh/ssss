package com.example.service;

import com.example.dao.PaymentDAO;
import com.example.model.Payment;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class PaymentService {
    private PaymentDAO paymentDAO;
    private ExportUtil exportUtil;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.exportUtil = new ExportUtil();
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        return paymentDAO.getColumnNamesAndTypes(tableName);
    }

    public List<Payment> getAllPayments() throws SQLException {
        return paymentDAO.getAllPayments();
    }

    public boolean updatePayment(Payment payment) throws SQLException {
        return paymentDAO.updatePayment(payment);
    }

    public boolean insertPayment(Payment payment) throws SQLException {
        return paymentDAO.insertPayment(payment);
    }

    public boolean deletePayment(int paymentID) throws SQLException {
        return paymentDAO.deletePayment(paymentID);
    }

    public void exportPaymentsToCSV(List<Payment> payments, File destination) throws IOException {
        exportUtil.exportPaymentsToCSV(payments, destination);
    }
}
