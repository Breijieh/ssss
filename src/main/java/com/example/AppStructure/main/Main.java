package com.example.AppStructure.main;

import com.example.AppStructure.header.Header;
import com.example.views.CarsView;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Main extends VBox {

    private static Main instance;
    private final StackPane contentPane;

    public Main() {
        instance = this;

        contentPane = new StackPane();
        contentPane.getChildren().add(new CarsView());
        // setBackground(new Background(new BackgroundFill(Theme.BLUE_COLOR,
        // CornerRadii.EMPTY, Insets.EMPTY)));
        getChildren().addAll(new Header(),contentPane);
    }

    public static Main getInstance() {
        return instance;
    }

    public void changeView(Node view) {
                contentPane.getChildren().add(view);
    }
}
