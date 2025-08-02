package com.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.loanshield.LoanApplicationData;
import com.loanshield.Main;

public class ResultsController {

    @FXML private Label scoreLabel;
    @FXML private Label summaryLabel;

    @FXML
    public void initialize() {
        int score = LoanApplicationData.creditScore;
        scoreLabel.setText(String.valueOf(score));

        String summary;

        if (score >= 75) {
            summary = "✅ Good application: Likely to be approved.";
        } else if (score >= 50) {
            summary = "⚠️ Moderate application: Approval with conditions.";
        } else {
            summary = "❌ High risk: Application may be declined.";
        }

        summaryLabel.setText(summary);
    }

    @FXML
    private void handleRestart() {
        Main.loadScene("/step1.fxml", "LoanShield - Step 1");
    }
}
