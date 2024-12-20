package com.example.AppStructure.header;

import com.example.AppStructure.aside.Logo;
import com.example.components.Theme;
import com.example.model.UserAccount;
import com.example.session.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class Header extends HBox {
    UserAccount user = SessionManager.getInstance().getCurrentUser();
    String username = user.getUsername();

    public Header() {
        getStyleClass().addAll("primary-bg", "border-header");
        setPadding(new Insets(10));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        Label separator = new Label("|");
        separator.setTextFill(Color.web("#DEE5F0"));
        separator.setPadding(new Insets(0, 10, 0, 10));
        separator.setFont(Theme.getPoppinsFont(200, 20));

        HBox userProfile = createUserProfile(username);
        userProfile.setEffect(Theme.createShadow());
        setEffect(Theme.createShadow());
        Label dateLabel = new Label("7 October 2023");
        dateLabel.setPadding(new Insets(5, 10, 5, 10));
        dateLabel.setFont(Theme.getPoppinsFont(500, 14));

        getChildren().addAll(new Logo());
    }

    private HBox createUserProfile(String userName) {
        ImageView profileIcon = new ImageView(
                new Image(getClass().getResourceAsStream("/com/example/images/profile.png")));
        profileIcon.setFitWidth(40);
        profileIcon.setFitHeight(40);
        profileIcon.styleProperty().set("-fx-border-radius: 50%;");

        Label nameLabel = new Label(userName);
        nameLabel.setPadding(new Insets(0, 10, 0, 10));
        nameLabel.setFont(Theme.getPoppinsFont(600, 14));

        HBox userProfile = new HBox(5, profileIcon, nameLabel);
        userProfile.setAlignment(Pos.CENTER);
        userProfile.setPadding(new Insets(5, 15, 5, 15));
        userProfile.getStyleClass().addAll("primary-bg", "rounded");

        return userProfile;
    }
}