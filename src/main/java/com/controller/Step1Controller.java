package com.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.Year;
import java.util.ResourceBundle;

public class Step1Controller implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField nicField;
    @FXML private TextField mobileField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;

    @FXML private TextField employmentTypeField;
    @FXML private TextField jobTitleField;
    @FXML private TextField employmentDurationField;
    @FXML private TextField monthlyIncomeField;

    @FXML private TextField requestedLoanAmountField;
    @FXML private TextField loanTypeField;
    @FXML private ComboBox<Integer> loanTermCombo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateLoanTermCombo();
    }

    private void populateLoanTermCombo() {
        int currentYear = Year.now().getValue();
        for (int i = 0; i < 30; i++) {
            loanTermCombo.getItems().add(currentYear + i);
        }
        loanTermCombo.setPromptText("Select Year");
    }
}
