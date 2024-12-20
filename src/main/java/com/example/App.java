package com.example;

import com.example.AppStructure.LoginStage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    private static Scene scene;

    @Override
    public void start(Stage stage) {

        LoginStage loginStage = new LoginStage();
        loginStage.show();
    }
    
}
