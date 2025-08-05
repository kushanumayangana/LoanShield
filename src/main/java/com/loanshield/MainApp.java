package com.loanshield;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL fxmlLocation = getClass().getResource("/step1.fxml");

            if (fxmlLocation == null) {
                System.err.println("ERROR: FXML file '/step1.fxml' not found in resources.");
                return;
            }

            Parent root = FXMLLoader.load(fxmlLocation);

            primaryStage.setTitle("LoanShield");
            primaryStage.setScene(new Scene(root, 800, 800));
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("An error occurred while loading the application:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
