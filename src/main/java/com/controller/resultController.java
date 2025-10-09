package com.controller;

import java.net.URL;
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
import javafx.stage.Stage;



//Encapsulation

public class resultController implements Initializable {

    @FXML private Label fullNameLabel;
    @FXML private Label nicLabel;
    @FXML private Label applicationDateLabel;
    @FXML private Label loanAmountLabel;
    @FXML private Label riskScoreLabel;
    @FXML private Label riskProfileLabel;
    @FXML private Label approvedAmountLabel;
    @FXML private Label interestRateLabel;
    @FXML private Label loanTermLabel;
    @FXML private Label monthlyPaymentLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize with default values or leave empty for now
        // The data will be set when the controller is loaded
    }
    //Encapsulation
    // Store NIC for Save action
    private String nicNumber;
    //Abstraction
    public void setNicNumber(String nic) {
        this.nicNumber = nic;
    }

    public void setApplicationData(String fullName, String nic, String applicationDate, 
                                 double loanAmount, int riskScore, int loanTerm) {
        fullNameLabel.setText(fullName);
        nicLabel.setText(nic);
        applicationDateLabel.setText(applicationDate);
        loanAmountLabel.setText(String.format("LKR %,.0f", loanAmount));
        
        riskScoreLabel.setText(String.valueOf(riskScore));
        
        // Set risk profile based on score
        if (riskScore >= 75) {
            riskProfileLabel.setText("Excellent Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else if (riskScore >= 65) {
            riskProfileLabel.setText("Good Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        } else {
            riskProfileLabel.setText("Fair Risk Profile");
            riskProfileLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 16px;");
        }
        
        approvedAmountLabel.setText(String.format("LKR %,.0f", loanAmount));
        interestRateLabel.setText("8.5% per annum");
        loanTermLabel.setText(loanTerm + " years");
        
        // Calculate monthly payment (simplified calculation)
        double monthlyRate = 0.085 / 12;
        int totalPayments = loanTerm * 12;
        double monthlyPayment = (loanAmount * monthlyRate * Math.pow(1 + monthlyRate, totalPayments)) / 
                              (Math.pow(1 + monthlyRate, totalPayments) - 1);
        monthlyPaymentLabel.setText(String.format("LKR %,.0f", monthlyPayment));
    }
    //Abstraction
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
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

   @FXML
   private void handleSave() {
       if (nicNumber == null || nicNumber.isEmpty()) {
           System.out.println("NIC number is not set.");
           return;
       }

       Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
       confirm.setTitle("Confirm Save");
       confirm.setHeaderText("Save approval result?");
       confirm.setContentText("This will store APPROVED status for NIC: " + nicNumber +
               "\nApproved Amount: " + approvedAmountLabel.getText());
       try {
           DialogPane pane = confirm.getDialogPane();
           pane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
           pane.getStyleClass().add("confirm-dialog");
           ButtonType yes = new ButtonType("Yes, Save", ButtonBar.ButtonData.OK_DONE);
           ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
           confirm.getButtonTypes().setAll(yes, cancel);
           Button yesBtn = (Button) pane.lookupButton(yes);
           if (yesBtn != null) yesBtn.getStyleClass().add("submit-button");
           Button cancelBtn = (Button) pane.lookupButton(cancel);
           if (cancelBtn != null) cancelBtn.getStyleClass().add("secondary-button");
       } catch (Exception ignored) {}

       Optional<ButtonType> choice = confirm.showAndWait();
       if (choice.isPresent() && choice.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
           boolean ok = DatabaseHelper.updateLatestApplication(nicNumber, "APPROVED", null, null);
           System.out.println(ok ? "Result saved for NIC: " + nicNumber : "Failed to save result for NIC: " + nicNumber);
       } else {
           System.out.println("Save cancelled by user.");
       }
   }

}