package com.example.AppStructure.aside;

import com.example.AppStructure.aside.menu.Menu;
import com.example.components.Theme;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;

public class Aside extends VBox {
    public Aside() {
        setSpacing(60);
        setMinWidth(230);
        setPadding(new Insets(20));
        getStyleClass().addAll("primary-bg", "border");

        getChildren().addAll(new Menu());
    }
}
