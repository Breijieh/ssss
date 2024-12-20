package com.example.components.general;

import com.example.components.Theme;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A customizable ComboBox with an associated label.
 *
 * @param <T> The type of the items in the ComboBox.
 */
public class CustomComboBox<T> extends VBox {
    private final ComboBox<T> comboBox;

    /**
     * Constructs a CustomComboBox with a specified label.
     *
     * @param labelText The text for the label associated with the ComboBox.
     */
    public CustomComboBox(String labelText) {
        setSpacing(5);
        Label label = new Label(labelText);
        label.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
        label.setTextFill(Color.web("#888888"));
        comboBox = new ComboBox<>();
        comboBox.setMinWidth(150); // Increased width for better visibility
        comboBox.setPromptText("Select");
        comboBox.setEffect(Theme.createShadow());
        getChildren().addAll(label, comboBox);
    }

    /**
     * Adds multiple items to the ComboBox.
     *
     * @param items The items to add.
     */
    @SafeVarargs
    public final void setItems(T... items) {
        comboBox.getItems().addAll(items);
    }

    /**
     * Retrieves the selected value from the ComboBox.
     *
     * @return The selected value of type T.
     */
    public T getValue() {
        return comboBox.getValue();
    }

    /**
     * Sets an event handler for action events on the ComboBox.
     *
     * @param handler The event handler to set.
     */
    public void setOnAction(EventHandler<ActionEvent> handler) {
        comboBox.setOnAction(handler);
    }

    /**
     * Provides access to the internal ComboBox for advanced configurations.
     *
     * @return The internal ComboBox instance.
     */
    public ComboBox<T> getComboBox() {
        return comboBox;
    }
}
