package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class Step1Controller {

    @FXML
    private void handleNext(ActionEvent event) {
        try {
            // Load step2.fxml from resources root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/step2.fxml"));
            Parent root = loader.load();

            // Get current stage from the event source (button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set new scene with step2.fxml root
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show alert dialog for error
        }
    }

}
