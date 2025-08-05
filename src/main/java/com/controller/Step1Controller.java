package com.controller;


import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.util.ResourceBundle;

import com.loanshield.LoanApplicationData;
import com.loanshield.LoanApprovalResult;
import com.loanshield.LoanEvaluator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Step1Controller implements Initializable {

    private final LoanApplicationData applicationData = new LoanApplicationData();

    // Personal Info
    @FXML private TextField fullNameField, nicField, mobileField, emailField, addressField;

    // Employment
    @FXML private TextField employmentTypeField, jobTitleField, employmentDurationField, employmentStartDate, monthlyIncomeField;

    // Loan Info
    @FXML private TextField requestedLoanAmountField, loanTypeField, loanTermField;
    @FXML private ComboBox<Integer> loanTermCombo;

    // Financial Info
    @FXML private RadioButton existingLoansYes;
    @FXML private VBox existingLoansFields;
    @FXML private TextField totalOutstandingAmount, monthlyInstallments, creditScore;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateLoanTermCombo();
        setupRadioButtons();
    }

    private void populateLoanTermCombo() {
        int currentYear = Year.now().getValue();
        for (int i = 0; i <= 30; i++) {
            loanTermCombo.getItems().add(currentYear + i);
        }
        loanTermCombo.setPromptText("Select Year");
    }

    private void setupRadioButtons() {
        existingLoansYes.setSelected(true);
        existingLoansFields.setVisible(true);
    }

    @FXML
    private void handleExistingLoansSelection() {
        boolean hasLoans = existingLoansYes.isSelected();
        existingLoansFields.setVisible(hasLoans);

        if (!hasLoans) {
            totalOutstandingAmount.clear();
            monthlyInstallments.clear();
        }
    }

    @FXML
    private void handleNextButton() {
        if (!validateForm()) {
            System.out.println("Validation failed.");
            return;
        }

        saveFormData();

        // Risk calculation
        LoanEvaluator evaluator = new LoanEvaluator();
        LoanApprovalResult approvalResult = evaluator.evaluate(applicationData);

        System.out.println("=== Approval Result ===");
        System.out.println("Approved: " + approvalResult.isApproved());
        System.out.println("Reason: " + approvalResult.getReason());
        System.out.println("Final Risk Score: " + approvalResult.getRiskScore());

    }

    private boolean validateForm() {
        boolean valid = true;

        valid &= validateField(fullNameField);
        valid &= validateField(nicField);
        valid &= validateField(mobileField);
        valid &= validateField(emailField);
        valid &= validateField(addressField);
        valid &= validateField(employmentTypeField);
        valid &= validateField(jobTitleField);
        valid &= validateField(employmentDurationField);
        valid &= validateField(employmentStartDate);
        valid &= validateField(monthlyIncomeField);
        valid &= validateField(requestedLoanAmountField);
        valid &= validateField(loanTypeField);
        valid &= validateField(loanTermField);
        valid &= validateField(creditScore);

        if (existingLoansYes.isSelected()) {
            valid &= validateField(totalOutstandingAmount);
            valid &= validateField(monthlyInstallments);
        }

        return valid;
    }

    private boolean validateField(TextField field) {
        if (field.getText().trim().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return false;
        } else {
            field.setStyle("");
            return true;
        }
    }

    private void saveFormData() {
        applicationData.setFullName(fullNameField.getText().trim());
        applicationData.setNic(nicField.getText().trim());
        applicationData.setPhone(mobileField.getText().trim());
        applicationData.setEmail(emailField.getText().trim());
        applicationData.setAddress(addressField.getText().trim());

        applicationData.setEmploymentType(employmentTypeField.getText().trim());
        applicationData.setJobTitle(jobTitleField.getText().trim());
        applicationData.setEmploymentDuration(Integer.parseInt(employmentDurationField.getText().trim()));
        applicationData.setMonthlyIncome(Double.parseDouble(monthlyIncomeField.getText().trim()));

        applicationData.setRequestedLoanAmount(Double.parseDouble(requestedLoanAmountField.getText().trim()));
        applicationData.setLoanType(loanTypeField.getText().trim());
        applicationData.setLoanTermYears(Integer.parseInt(loanTermField.getText().trim()));
        applicationData.setHasExistingLoans(existingLoansYes.isSelected());
        

        if (applicationData.hasExistingLoans()) {
            applicationData.setTotalOutstandingLoan(Double.parseDouble(totalOutstandingAmount.getText().trim()));
            applicationData.setMonthlyInstallments(Double.parseDouble(monthlyInstallments.getText().trim()));
        }

        applicationData.setCreditScore(Integer.parseInt(creditScore.getText().trim()));
        applicationData.setApplicationDate(LocalDate.now().toString());
        applicationData.setApplicationStatus("SUBMITTED");

        System.out.println("=== Form Saved ===");
        System.out.println("Applicant: " + applicationData.getFullName());
        System.out.println("Loan Amount: " + applicationData.getRequestedLoanAmount());
    }
}
