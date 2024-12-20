package com.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.model.UserAccount;

public class UserDAO extends AbstractDAO<UserAccount> {

    @Override
    protected UserAccount mapRowToObject(ResultSet resultSet) throws SQLException {
        return new UserAccount(
                resultSet.getString("username"),
                resultSet.getString("password"));
    }

    public UserAccount getUserByUsernameAndPassword(String username, String password) {
        String query = "SELECT * FROM user_account WHERE username = ? AND password = ?";
        return getOne(query, username, password);
    }
}
