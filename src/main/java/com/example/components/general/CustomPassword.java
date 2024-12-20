package com.example.components.general;

import com.example.components.Theme;

import javafx.scene.control.PasswordField;

public class CustomPassword extends PasswordField {
    public CustomPassword(String text) {
        setPrefWidth(150);
        setPromptText(text);
        setEffect(Theme.createShadow());
        getStyleClass().add("text-field");

    }
}
