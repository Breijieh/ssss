package com.example.session;

import com.example.model.UserAccount;

public class SessionManager {
    private static SessionManager instance;
    private UserAccount currentUser;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(UserAccount user) {
        this.currentUser = user;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }
}
