package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

// ENCAPSULATION: This class demonstrates encapsulation by:
// 1. Using private fields for UI components (TextField, PasswordField, Label)
// 2. Providing private methods for internal functionality
// 3. Using FXML annotations to bind UI components while maintaining encapsulation
public class RegisterController {
    // ENCAPSULATION: Private fields with FXML annotations for UI component binding
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField secretKeyField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String u = usernameField.getText();
        String p = passwordField.getText();
        String s = secretKeyField.getText();
        if (u == null || u.isBlank() || p == null || p.isBlank() || s == null || s.isBlank()) {
            messageLabel.setText("Fill all fields");
            return;
        }
        if (AuthRepository.usernameExists(u)) {
            messageLabel.setText("Username already exists");
            return;
        }
        boolean ok = AuthRepository.register(u, p, s);
        if (ok) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Registration successful! Redirecting to login...");
            // Automatically navigate to login page after successful registration
            goTo("/login.fxml", event);
        } else {
            messageLabel.setText("Registration failed. Check secret or username taken.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        goTo("/login.fxml", event);
    }

    // ENCAPSULATION: Private method for internal navigation logic
    private void goTo(String resource, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

