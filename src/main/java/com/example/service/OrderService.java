package com.example.service;

import com.example.dao.OrderDAO;
import com.example.model.Order;
import com.example.util.ExportUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderService {
    private OrderDAO orderDAO;
    private ExportUtil exportUtil;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.exportUtil = new ExportUtil();
    }

    public Map<String, String> getColumnNamesAndTypes(String tableName) throws SQLException {
        return orderDAO.getColumnNamesAndTypes(tableName);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.getAllOrders();
    }

    public boolean updateOrder(Order order) throws SQLException {
        return orderDAO.updateOrder(order);
    }

    public boolean insertOrder(Order order) throws SQLException {
        return orderDAO.insertOrder(order);
    }

    public boolean deleteOrder(int orderID) throws SQLException {
        return orderDAO.deleteOrder(orderID);
    }

    public void exportOrdersToCSV(List<Order> orders, File destination) throws IOException {
        exportUtil.exportOrdersToCSV(orders, destination);
    }

    public Order getOrderById(int orderID) throws SQLException {
        return orderDAO.getOrderById(orderID);
    }

    public List<Order> getOrdersByEmployeeId(int employeeID) throws SQLException {
        return orderDAO.getOrdersByEmployeeId(employeeID);
    }
}
