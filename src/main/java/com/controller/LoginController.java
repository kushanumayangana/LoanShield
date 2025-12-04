package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

// ENCAPSULATION: This class demonstrates encapsulation by:
// 1. Using private fields for UI components (TextField, PasswordField, Label)
// 2. Providing private methods for internal functionality
// 3. Using FXML annotations to bind UI components while maintaining encapsulation
public class LoginController {
    // ENCAPSULATION: Private fields with FXML annotations for UI component binding
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String u = usernameField.getText();
        String p = passwordField.getText();
        if (u == null || u.isBlank() || p == null || p.isBlank()) {
            messageLabel.setText("Enter username and password");
            return;
        }
        if (AuthRepository.login(u, p)) {
            goTo("/Step1.fxml", event);
        } else {
            messageLabel.setText("Invalid credentials");
        }
    }

    @FXML
    private void handleGoRegister(ActionEvent event) {
        goTo("/register.fxml", event);
    }

    // ENCAPSULATION: Private method for internal navigation logic
    private void goTo(String resource, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            fitToScreen(stage);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fitToScreen(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setMaximized(true);
        stage.centerOnScreen();
    }
}

