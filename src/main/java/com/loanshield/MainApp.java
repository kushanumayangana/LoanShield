package com.loanshield;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// INHERITANCE: This class extends the Application class from JavaFX
//  inherits all methods and properties from Application class to main class 
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginPage();
    }

    public void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showStep1Page() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Step1.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("LoanShield");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
