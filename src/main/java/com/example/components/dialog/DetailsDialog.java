package com.example.components.dialog;

import com.example.components.Theme;
import com.example.components.general.CustomButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DetailsDialog extends StackPane {

    private VBox modalContent;
    private Label titleLabel;
    private Pane customContent;
    private CustomButton closeButton;

    // Separate stage for this dialog
    private Stage dialogStage;

    public DetailsDialog() {
        // Create a separate stage for the details dialog
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UNDECORATED);

        modalContent = new VBox(15);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setPadding(new Insets(20));
        modalContent.setMaxWidth(600);
        modalContent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        modalContent.setEffect(Theme.createShadow());
        modalContent.setMaxHeight(Region.USE_PREF_SIZE);
        modalContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        titleLabel = new Label("Details");
        titleLabel.setFont(Theme.getPoppinsFont(500, 20));
        titleLabel.setTextFill(Theme.GRAY_COLOR);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setWrapText(true);

        customContent = new Pane();
        customContent.setMinHeight(100);

        closeButton = new CustomButton("Close");
        closeButton.setMinWidth(80);
        closeButton.setOnAction(event -> hide());

        modalContent.getChildren().addAll(titleLabel, customContent, closeButton);
        this.getChildren().add(modalContent);

        // Create a scene with this as the root and set it to dialogStage
        Scene scene = new Scene(this);
        dialogStage.setScene(scene);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setCustomContent(Pane content) {
        customContent = content;
        modalContent.getChildren().set(1, customContent);
    }

    /**
     * Simply shows the dialog stage without animations.
     */
    public void showWithAnimation() {
        dialogStage.show();
    }

    /**
     * Hides the dialog stage without animations.
     */
    public void hideWithAnimation() {
        dialogStage.hide();
    }

    public void show() {
        showWithAnimation();
    }

    public void hide() {
        hideWithAnimation();
    }
}
