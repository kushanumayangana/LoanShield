package com.loanshield;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            loadScene("/step1.fxml", "LoanShield - Step 1");
            System.out.println("Application started successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }

    /**
     * Loads the given FXML file and sets the stage title.
     *
     * @param fxmlPath   the path to the FXML file
     * @param windowTitle the title of the window
     */
    public static void loadScene(String fxmlPath, String windowTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Apply shared stylesheet
            String css = Main.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle(windowTitle);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
