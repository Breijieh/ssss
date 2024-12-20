package com.example.components.general;

import com.example.components.Theme;

import javafx.scene.control.TextField;

public class CustomInput extends TextField {
    public CustomInput(String text) {
        setPrefWidth(150);
        setPromptText(text);
        setEffect(Theme.createShadow());
        getStyleClass().add("text-field");
    }


    public void clearValidation() {
        getStyleClass().removeAll("valid", "invalid");
    }

    public void setValid(boolean valid) {
        getStyleClass().removeAll("valid", "invalid");
        if (valid) {
            getStyleClass().add("valid");
        } else {
            getStyleClass().add("invalid");
        }
    }
}
