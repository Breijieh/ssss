package com.example.components.general;

import com.example.components.Theme;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CustomSearchBox extends VBox {
    private String dataType = "VARCHAR";
    private CustomInput searchField;
    private String columnText;

    public CustomSearchBox(String labelText, String type) {
        setSpacing(5);

        Label label = new Label(labelText);
        columnText = labelText;
        label.setFont(Theme.getPoppinsFont(600, 17));
        label.setTextFill(Color.web("#888888"));

        searchField = new CustomInput("");
        searchField.setPrefWidth(150);
        dataType = type;
        switch (type.toUpperCase()) {
            case "DECIMAL":
            case "DOUBLE":
            case "INT":
                break;
            case "VARCHAR":
                break;
            case "DATE":
                break;
            default:
                searchField.setPromptText("Data");
                break;
        }

        getChildren().addAll(searchField, label);
    }

    public String getText() {
        return searchField.getText();
    }

    public void setText(String text) {
        searchField.setText(text);
    }

    public String getDataType() {
        return this.dataType;
    }

    public String getColumnText() {
        return columnText;
    }

    public CustomInput getSearchField() {
        return searchField;
    }

    // Clear validation feedback
    public void clearValidation() {
        searchField.getStyleClass().removeAll("valid", "invalid");
    }

    // Apply validation feedback
    public void setValid(boolean valid) {
        searchField.getStyleClass().removeAll("valid", "invalid");
        if (valid) {
            if (!searchField.getStyleClass().contains("valid")) {
                searchField.getStyleClass().add("valid");
            }
            searchField.setTooltip(null); // Clear any error tooltip on valid input
        } else {
            if (!searchField.getStyleClass().contains("invalid")) {
                searchField.getStyleClass().add("invalid");
            }
        }
    }

}
