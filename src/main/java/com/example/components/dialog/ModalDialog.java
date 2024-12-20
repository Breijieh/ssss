package com.example.components.dialog;

import com.example.components.Theme;
import com.example.components.general.CustomButton;
import com.example.components.general.CustomSearchBox;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ModalDialog extends StackPane {

    private VBox modalContent; // Container for dialog content
    private Map<String, CustomSearchBox> customSearchBoxMap; // Map of fields to CustomSearchBox
    private Object entity; // The entity to insert
    private Runnable onConfirm; // Action on confirmation
    private Runnable onCancel; // Action on cancellation
    private Label errorLabel; // Label to display validation or SQL errors

    // A separate stage for this dialog
    private Stage dialogStage;

    public ModalDialog() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UNDECORATED);

        modalContent = new VBox(10);
        modalContent.setStyle("-fx-background-color: white; "
                + "-fx-padding: 20; "
                + "-fx-border-radius: 10; "
                + "-fx-background-radius: 10; "
                + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 10, 0.5, 0, 0);");
        modalContent.setMaxSize(400, Region.USE_PREF_SIZE);
        modalContent.setAlignment(Pos.TOP_LEFT);

        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);

        this.getChildren().add(modalContent);
        StackPane.setAlignment(modalContent, Pos.CENTER);

        Scene scene = new Scene(this);
        dialogStage.setScene(scene);

        // Removed the this.setVisible(false) line
    }

    public void setContent(
            Object entity,
            List<FieldDefinition> fieldDefinitions,
            String dialogTitle,
            String confirmButtonText,
            Runnable onConfirm,
            Runnable onCancel) {

        this.entity = entity;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;

        modalContent.getChildren().clear();

        // Modal Header
        VBox modalHeader = new VBox(5);
        Label modalTitle = new Label(dialogTitle);
        modalTitle.setFont(Theme.getPoppinsFont(400, 20));
        modalTitle.setTextFill(Theme.GRAY_COLOR);

        modalHeader.getChildren().addAll(modalTitle);
        modalContent.getChildren().add(modalHeader);

        // Initialize errorLabel
        errorLabel.setText("");
        errorLabel.setVisible(false);
        modalContent.getChildren().add(errorLabel);

        customSearchBoxMap = new HashMap<>();

        // Create form fields dynamically
        for (FieldDefinition fieldDef : fieldDefinitions) {
            String fieldName = fieldDef.getFieldName();
            String displayName = fieldDef.getDisplayName();
            String dataType = fieldDef.getDataType();
            Object value = fieldDef.getValue();

            CustomSearchBox customSearchBox = new CustomSearchBox(displayName, dataType);
            customSearchBox.setText(value != null ? value.toString() : "");
            modalContent.getChildren().add(customSearchBox);
            customSearchBoxMap.put(fieldName, customSearchBox);
        }

        // Buttons
        CustomButton confirmButton = new CustomButton(confirmButtonText);
        CustomButton cancelButton = new CustomButton("Cancel", true);

        HBox buttonsBox = new HBox(10);
        buttonsBox.getChildren().addAll(confirmButton, cancelButton);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);

        // Confirm Button Action
        confirmButton.setOnAction(event -> handleInsert(fieldDefinitions));

        // Cancel Button Action
        cancelButton.setOnAction(event -> handleCancel());

        modalContent.getChildren().addAll(buttonsBox);
    }

    private void handleInsert(List<FieldDefinition> fieldDefinitions) {
        boolean hasError = false;
        errorLabel.setText("");
        errorLabel.setVisible(false);

        for (FieldDefinition fieldDef : fieldDefinitions) {
            String fieldName = fieldDef.getFieldName();
            CustomSearchBox customSearchBox = customSearchBoxMap.get(fieldName);
            String newValue = customSearchBox.getText();

            try {
                // Validate and convert input
                Object convertedValue = validateAndConvert(fieldDef.getDataType(), newValue, fieldDef.getDisplayName());

                // Update entity field dynamically using reflection
                updateEntityField(entity, fieldName, convertedValue);

                // Mark field as valid
                customSearchBox.setValid(true);
                customSearchBox.getSearchField().setTooltip(null);

            } catch (ValidationException ve) {
                // Validation error
                hasError = true;
                customSearchBox.setValid(false);
            } catch (Exception e) {
                // General error
                hasError = true;
                errorLabel.setText("An unexpected error occurred: " + simplifyErrorMessage(e.getMessage()));
                errorLabel.setVisible(true);
                e.printStackTrace();
            }
        }

        if (!hasError) {
            // If no errors, run confirm action
            if (onConfirm != null) {
                onConfirm.run();
            }
            dialogStage.hide();
        }
    }

    private void handleCancel() {
        // Clear all errors
        errorLabel.setText("");
        errorLabel.setVisible(false);
        customSearchBoxMap.values().forEach(CustomSearchBox::clearValidation);

        if (onCancel != null) {
            onCancel.run();
        }
        hideWithAnimation();
    }

    private Object validateAndConvert(String dataType, String value, String displayName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(displayName + " cannot be empty.");
        }

        try {
            switch (dataType.toUpperCase()) {
                case "INT":
                    return Integer.parseInt(value);
                case "DOUBLE":
                    return Double.parseDouble(value);
                case "BOOLEAN":
                    return Boolean.parseBoolean(value);
                case "VARCHAR":
                case "STRING":
                    return value;
                case "DATE":
                    return parseDateFlexible(value, displayName);
                default:
                    throw new ValidationException("Unsupported data type for " + displayName + ".");
            }
        } catch (NumberFormatException e) {
            throw new ValidationException(displayName + " has an invalid format.");
        }
    }

    private void updateEntityField(Object entity, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = entity.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        if (javafx.beans.property.Property.class.isAssignableFrom(field.getType())
                || field.getType().getName().contains("Property")) {
            Object property = field.get(entity);
            if (property instanceof javafx.beans.property.Property) {
                ((javafx.beans.property.Property<Object>) property).setValue(value);
            } else {
                throw new IllegalArgumentException(
                        "Unsupported property type for field: " + fieldName);
            }
        } else {
            field.set(entity, value);
        }
    }

    private String simplifyErrorMessage(String message) {
        if (message.contains(":")) {
            return message.substring(message.lastIndexOf(":") + 1).trim();
        }
        return message;
    }

    public void showWithAnimation() {
        // No animations. Just show the stage.
        dialogStage.show();
    }

    public void hideWithAnimation() {
        // No animations. Just hide the stage.
        dialogStage.hide();
    }

    public void show() {
        showWithAnimation();
    }

    public void hide() {
        hideWithAnimation();
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    private java.util.Date parseDateFlexible(String dateStr, String displayName) throws ValidationException {
        try {
            return com.example.util.DateUtil.parseDateFlexible(dateStr);
        } catch (java.text.ParseException e) {
            throw new ValidationException("Invalid date format for " + displayName
                    + ". Accepted formats: dd-MM-yyyy, dd MM yyyy, dd/MM/yyyy, dd.MM.yyyy.");
        }
    }
}
