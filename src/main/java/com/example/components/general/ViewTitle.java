package com.example.components.general;

import com.example.components.Theme;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ViewTitle extends VBox {

    public ViewTitle(String titleText, String subtitleText) {
        Label titleLabel = new Label(titleText);
        titleLabel.setFont(Theme.getPoppinsFont(600, 26));

        Label subtitleLabel = new Label(subtitleText);
        subtitleLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 14));
        setSpacing(5);
        getChildren().addAll(titleLabel, subtitleLabel);

        titleLabel.getStyleClass().add("view-title");
        subtitleLabel.getStyleClass().add("view-subtitle");

    }
}
