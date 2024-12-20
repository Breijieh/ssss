package com.example.AppStructure;

import com.example.AppStructure.aside.Aside;
import com.example.AppStructure.main.Main;
import com.example.components.dialog.FieldDefinition;
import com.example.components.dialog.MessageDialog;
import com.example.components.dialog.ModalDialog;
import com.example.components.dialog.DetailsDialog;
import com.example.session.SessionManager;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * The main application stage that manages the primary scene,
 * modals, messages, confirmations, and theme settings.
 */
public class AppStage extends Stage {

    private static AppStage instance;

    private final StackPane root;

    private final ModalDialog modalDialog;
    private final MessageDialog messageDialog;
    private final DetailsDialog detailsDialog;

    private Scene scene;

    private boolean isDarkTheme = false;

    private static final String PREF_THEME = "theme";
    private final Preferences prefs = Preferences.userNodeForPackage(AppStage.class);

    /**
     * Constructor initializes the main stage with all components.
     */
    public AppStage() {

        instance = this;

        root = new StackPane();

        HBox mainContent = new HBox();

        Main main = new Main();
        HBox.setHgrow(main, Priority.ALWAYS);

        mainContent.getChildren().addAll(new Aside(), main);

        modalDialog = new ModalDialog();
        messageDialog = new MessageDialog();
        detailsDialog = new DetailsDialog();

        root.getChildren().addAll(mainContent);

        scene = new Scene(root, 1400, 800);

        scene.getStylesheets().add(getClass().getResource("/com/example/css/style.css").toExternalForm());

        boolean dark = prefs.getBoolean(PREF_THEME, false);
        if (dark) {

            scene.getStylesheets().add(getClass().getResource("/com/example/css/dark-theme.css").toExternalForm());
            isDarkTheme = true;
        } else {
            isDarkTheme = false;
        }

        this.setScene(scene);

        this.setTitle("Main Application - " + SessionManager.getInstance().getCurrentUser().getUsername());
    }

    /**
     * Retrieves the singleton instance of AppStage.
     *
     * @return The AppStage instance.
     */
    public static AppStage getInstance() {
        return instance;
    }

    public static StackPane getRoot() {
        return instance.root;
    }

    public void showModal(
            Object entity,
            List<FieldDefinition> fieldDefinitions,
            String dialogTitle,
            String confirmButtonText,
            Runnable onConfirm,
            Runnable onCancel) {
        modalDialog.setContent(entity, fieldDefinitions, dialogTitle, confirmButtonText, onConfirm, onCancel);
        modalDialog.show();
    }

    public void hideModal() {
        modalDialog.hide();
    }

    public void showMessage(String title, String message, MessageDialog.MessageType type) {
        messageDialog.show(title, message, type);
    }

    public void toggleTheme() {
        if (isDarkTheme) {

            scene.getStylesheets().remove(getClass().getResource("/com/example/css/dark-theme.css").toExternalForm());
            isDarkTheme = false;
            prefs.putBoolean(PREF_THEME, false);
        } else {

            if (!scene.getStylesheets()
                    .contains(getClass().getResource("/com/example/css/dark-theme.css").toExternalForm())) {
                scene.getStylesheets().add(getClass().getResource("/com/example/css/dark-theme.css").toExternalForm());
            }
            isDarkTheme = true;
            prefs.putBoolean(PREF_THEME, true);
        }
    }

    public void setTheme(boolean dark) {
        if (dark && !isDarkTheme) {
            toggleTheme();
        } else if (!dark && isDarkTheme) {
            toggleTheme();
        }
    }

    public boolean isDarkTheme() {
        return isDarkTheme;
    }

    public ModalDialog getModalDialog() {
        return modalDialog;
    }

    public DetailsDialog getDetailsDialog() {
        return detailsDialog;
    }
}
