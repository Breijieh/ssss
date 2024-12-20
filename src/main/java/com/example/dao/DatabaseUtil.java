package com.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/Cars";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "root", "");
    }
}
