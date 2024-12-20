package com.example.components.general;

import com.example.components.Theme;

import javafx.scene.control.Label;

public class SectionTitle extends Label {
    public SectionTitle(String title) {
        setText(title);
        setFont(Theme.getPoppinsFont(600, 18));
        setTextFill(Theme.NAVY_COLOR);
        getStyleClass().add("section-title");

    }
}
