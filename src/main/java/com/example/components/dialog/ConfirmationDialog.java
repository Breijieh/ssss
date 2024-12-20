package com.example.components.dialog;

import com.example.components.Theme;
import com.example.components.general.CustomButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class ConfirmationDialog extends StackPane {

    private VBox modalContent;
    private Label titleLabel;
    private Label messageLabel;
    private CustomButton yesButton;
    private CustomButton noButton;

    public ConfirmationDialog() {
        // Semi-transparent background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        this.setPickOnBounds(true);
        this.setVisible(false);
        this.setAlignment(Pos.CENTER);

        // Modal content container
        modalContent = new VBox(15);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setPadding(new Insets(20));
        modalContent.setMaxWidth(400);
        modalContent.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        modalContent.setEffect(Theme.createShadow());

        // Prevent VBox from growing vertically
        modalContent.setMaxHeight(Region.USE_PREF_SIZE);
        modalContent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        // Title label
        titleLabel = new Label();
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Theme.BLUE_COLOR);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setWrapText(true); // Ensure long titles wrap appropriately

        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Poppins", 16));
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setTextFill(Color.BLACK);

        // Yes and No Buttons
        yesButton = new CustomButton("Yes");
        noButton = new CustomButton("No", true);
        yesButton.setMinWidth(80);
        noButton.setMinWidth(80);

        // Arrange buttons
        HBox buttonsBox = new HBox(10, yesButton, noButton);
        buttonsBox.setAlignment(Pos.CENTER);

        // Arrange components
        modalContent.getChildren().addAll(titleLabel, messageLabel, buttonsBox);
        this.getChildren().add(modalContent);
    }

    /**
     * Displays the confirmation dialog with the specified title and message.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     * @param onYes   Action to perform if the user clicks "Yes".
     * @param onNo    Action to perform if the user clicks "No".
     */
    public void show(String title, String message, Runnable onYes, Runnable onNo) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        yesButton.setOnAction(event -> {
            if (onYes != null) {
                onYes.run();
            }
            hide();
        });

        noButton.setOnAction(event -> {
            if (onNo != null) {
                onNo.run();
            }
            hide();
        });

        this.setVisible(true);
    }

    /**
     * Hides the dialog.
     */
    public void hide() {
        this.setVisible(false);
    }
}
