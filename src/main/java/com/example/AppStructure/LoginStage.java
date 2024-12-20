package com.example.AppStructure;

import com.example.components.Theme;
import com.example.components.general.CustomButton;
import com.example.components.general.CustomInput;
import com.example.components.general.CustomPassword;
import com.example.dao.UserDAO;
import com.example.model.UserAccount;
import com.example.session.SessionManager;
// import com.mysql.cj.log.Log;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginStage extends Stage {
        private static LoginStage instance;

        public LoginStage() {
                instance = this;
                FlowPane sceneContainer = new FlowPane();

                HBox container = new HBox();
                container.setPrefSize(800, 500);

                // Right side setup
                VBox rightSide = new VBox();
                rightSide.setAlignment(Pos.CENTER);
                rightSide.setPadding(new Insets(40));
                rightSide.setSpacing(20);
                rightSide.setBackground(
                                new Background(new BackgroundFill(Color.WHITE, null,
                                                Insets.EMPTY)));

                Label formTitle = new Label("Login");
                formTitle.setFont(Theme.getPoppinsFont(600, 24));
                formTitle.setTextFill(Theme.NAVY_COLOR);

                Label formSubtitle = new Label("Enter the essential information to have the access");
                formSubtitle.setFont(Theme.getPoppinsFont(400, 14));
                formSubtitle.setTextFill(Color.GRAY);

                CustomInput usernameField = new CustomInput("Username");
                usernameField.setPromptText("Email");
                usernameField.setPrefWidth(300);

                CustomPassword passwordField = new CustomPassword("Password");
                passwordField.setPrefWidth(300);

                CustomButton loginButton = new CustomButton("Log in");
                loginButton.setStyle("-fx-padding: 10 20;");
                loginButton.setPrefWidth(350);

                rightSide.getChildren().addAll(formTitle, formSubtitle, usernameField, passwordField, loginButton);

                container.getChildren().addAll(rightSide);

                sceneContainer.getChildren().add(container);
                container.setBackground(new Background(new BackgroundFill(
                                Color.TRANSPARENT,
                                null,
                                Insets.EMPTY)));
                container.setAlignment(Pos.CENTER);
                container.setEffect(Theme.createShadow());
                sceneContainer.setAlignment(Pos.CENTER);
                sceneContainer.setBackground(
                                new Background(new BackgroundFill(Theme.BACKGROUND_COLOR, null,
                                                Insets.EMPTY)));
                Scene scene = new Scene(sceneContainer);
                scene.getStylesheets().add(getClass().getResource("/com/example/css/style.css").toExternalForm());

                // stage properties
                setScene(scene);
                setTitle("SababaAuto");
                setMaximized(true);
                // _stage properties

                // Login button event
                loginButton.setOnAction(event -> {
                        String username = usernameField.getText();
                        String password = passwordField.getText();

                        UserDAO userDAO = new UserDAO();
                        UserAccount user = userDAO.getUserByUsernameAndPassword(username, password);

                        if (user != null) {
                                SessionManager.getInstance().setCurrentUser(user);

                                this.close();
                                AppStage appScene = new AppStage();
                                appScene.show();
                        } else {
                                formSubtitle.setText("Invalid username or password. Try again.");
                                formSubtitle.setTextFill(Color.RED);
                        }
                });
                // _Login button event
        }

        public static LoginStage getInstance() {
                return instance;
        }
}
