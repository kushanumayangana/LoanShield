package com.loanshield;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file from resources
            Parent root = FXMLLoader.load(getClass().getResource("/step1.fxml"));

            // Set up the primary stage
            primaryStage.setTitle("LoanShield");
            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
