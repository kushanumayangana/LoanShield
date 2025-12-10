package com.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


//Encapsulation

public class rejectController implements Initializable {

    @FXML private Label fullNameLabel;
    @FXML private Label nicLabel;
    @FXML private Label applicationDateLabel;
    @FXML private Label loanAmountLabel;
    @FXML private Label riskScoreLabel;
    @FXML private Label riskProfileLabel;
    @FXML private VBox rejectionReasonsContainer;
    @FXML private Label generatedDateLabel;
    @FXML private Label footerDateLabel;
    //Encapsulation
    // New Save Button (make sure to add this to your reject.fxml and fx:id it properly)
    @FXML private Button saveButton;

    // Store NIC number received from Step1Controller
    private String nicNumber;


   

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization code if needed
    }
     //Abstraction:
    // Method to receive full data (already exists)
    public void setApplicationData(String fullName, String nic, String applicationDate, 
                                 double loanAmount, int riskScore, List<String> rejectionReasons) {
        fullNameLabel.setText(fullName);
        nicLabel.setText(nic);
        applicationDateLabel.setText(applicationDate);
        loanAmountLabel.setText(String.format("LKR %,.0f", loanAmount));

        // Set current date for generated and footer labels
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        if (generatedDateLabel != null) {
            generatedDateLabel.setText("Generated: " + currentDate);
        }
        if (footerDateLabel != null) {
            footerDateLabel.setText("Date: " + currentDate);
        }

        riskScoreLabel.setText(String.valueOf(riskScore));

        if (riskScore >= 75) {
            riskProfileLabel.setText("Excellent Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else if (riskScore >= 65) {
            riskProfileLabel.setText("Good Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else if (riskScore >= 50) {
            riskProfileLabel.setText("Fair Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 16px;");
        } else {
            riskProfileLabel.setText("Poor Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 16px;");
        }

        addRejectionReasons(rejectionReasons);
    }
     //Abstraction:
    // New method to receive NIC from Step1Controller (UPDATED PART)
    public void setNicNumber(String nic) {
        this.nicNumber = nic;
        nicLabel.setText(nic);  // Optionally update label here if not set before
    }
    //Encapsulation:
    private void addRejectionReasons(List<String> reasons) {
        rejectionReasonsContainer.getChildren().clear();

        if (reasons != null && !reasons.isEmpty()) {
            for (String reason : reasons) {
                HBox reasonBox = new HBox(10);
                reasonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Label bulletPoint = new Label("•");
                bulletPoint.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold; -fx-font-size: 14px;");

                Label reasonLabel = new Label(reason);
                reasonLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px;");
                reasonLabel.setWrapText(true);

                reasonBox.getChildren().addAll(bulletPoint, reasonLabel);
                rejectionReasonsContainer.getChildren().add(reasonBox);
            }
        } else {
            HBox reasonBox = new HBox(10);
            reasonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label bulletPoint = new Label("•");
            bulletPoint.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label reasonLabel = new Label("Risk score below minimum threshold for approval");
            reasonLabel.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 14px;");

            reasonBox.getChildren().addAll(bulletPoint, reasonLabel);
            rejectionReasonsContainer.getChildren().add(reasonBox);
        }
    }


    //Abstraction:
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            // Load from classpath root
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Step1.fxml"));
            Parent step1Root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(step1Root));
            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading Step1.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
     //Abstraction:
    // NEW method: Handle Save Button click to save rejection info into SQLite DB (UPDATED PART)
    @FXML
    private void handleSaveButton(ActionEvent event) {
        if (nicNumber == null || nicNumber.isEmpty()) {
            System.out.println("NIC number is not set.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Save");
        confirm.setHeaderText("Save rejection result?");
        confirm.setContentText("This will store REJECTED status for NIC: " + nicNumber);
        try {
            DialogPane pane = confirm.getDialogPane();
            pane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            pane.getStyleClass().add("confirm-dialog");
            ButtonType yes = new ButtonType("Yes, Save", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(yes, cancel);
        } catch (Exception ignored) {}

        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isPresent() && choice.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            boolean ok = DatabaseHelper.updateLatestApplication(nicNumber, "REJECTED", null, null);
            System.out.println(ok ? "Rejection saved successfully for NIC: " + nicNumber : "Failed to save rejection for NIC: " + nicNumber);
            if (ok) saveButton.setDisable(true);
        } else {
            System.out.println("Save cancelled by user.");
        }
    }
}
