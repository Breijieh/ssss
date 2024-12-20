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

public class MessageDialog extends StackPane {

    private VBox modalContent;
    private Label titleLabel;
    private Label messageLabel;
    private CustomButton okButton;

    public enum MessageType {
        INFORMATION,
        ERROR
    }

    public MessageDialog() {
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
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 17));
        titleLabel.setTextFill(Theme.GRAY_COLOR);
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setWrapText(true); // Ensure long titles wrap appropriately

        // Message label
        messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Poppins", 16));
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setTextFill(Color.BLACK);

        // OK Button
        okButton = new CustomButton("OK");
        okButton.setMinWidth(80);
        okButton.setOnAction(event -> hide());

        // Arrange components
        modalContent.getChildren().addAll(titleLabel, messageLabel, okButton);
        this.getChildren().add(modalContent);
    }

    /**
     * Displays the dialog with the specified title, message, and type.
     *
     * @param title   The title of the dialog.
     * @param message The message to display.
     * @param type    The type of the message (INFORMATION or ERROR).
     */
    public void show(String title, String message, MessageType type) {
        titleLabel.setText(title);
        messageLabel.setText(message);

        // Adjust styles based on message type
        switch (type) {
            case INFORMATION:
                titleLabel.setTextFill(Theme.BLUE_COLOR);
                break;
            case ERROR:
                titleLabel.setTextFill(Color.RED);
                break;
        }

        this.setVisible(true);
    }

    /**
     * Hides the dialog.
     */
    public void hide() {
        this.setVisible(false);
    }
}
