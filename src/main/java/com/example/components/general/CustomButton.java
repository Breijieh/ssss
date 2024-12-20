package com.example.components.general;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomButton extends Button {

    public CustomButton(String text) {
        this(text, null, false);
    }

    public CustomButton(String text, String iconName) {
        this(text, iconName, false);
    }

    public CustomButton(String text, boolean isOutlined) {
        this(text, null, isOutlined);
    }

    public CustomButton(String text, String iconName, boolean isOutlined) {
        super(text);
        setCursor(Cursor.HAND);
        setPadding(new Insets(10));

        if (isOutlined) {
            getStyleClass().addAll("custom-button", "outlined");
        } else {
            getStyleClass().add("custom-button");
        }

        if (iconName != null) {
            if (iconName.startsWith("fa-")) {
            } else {
                try {
                    Image icon = new Image(getClass().getResourceAsStream("/com/example/images/" + iconName));
                    if (icon.isError()) {
                        throw new IllegalArgumentException("Error loading icon: " + iconName);
                    }
                    ImageView iconView = new ImageView(icon);
                    iconView.setFitHeight(16);
                    iconView.setFitWidth(16);
                    setGraphic(iconView);
                    setGraphicTextGap(8);
                } catch (Exception e) {
                    System.out.println("Icon not found or error loading icon: " + iconName);
                    e.printStackTrace();
                }
            }
        }
    }
}
