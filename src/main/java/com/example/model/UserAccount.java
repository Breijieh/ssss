package com.example.model;

public class UserAccount {
    private String username;
    private String password;

    // Constructor
    public UserAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters (and setters if needed)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
