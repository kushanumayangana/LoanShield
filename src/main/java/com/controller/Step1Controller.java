package com.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.loanshield.LoanApplicationData;
import com.loanshield.LoanApprovalResult;
import com.loanshield.LoanEvaluator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// INHERITANCE & POLYMORPHISM: This class implements the Initializable interface
// This demonstrates interface implementation (a form of inheritance) and polymorphism
// The initialize method is a contract that must be implemented (polymorphism)
public class Step1Controller implements Initializable {

    private final LoanApplicationData applicationData = new LoanApplicationData();

    // Personal Info
    @FXML private TextField fullNameField, nicField;

    // Employment
    @FXML private TextField employmentDurationField, monthlyIncomeField;

    // Loan Info
    @FXML private TextField requestedLoanAmountField;
    @FXML private ComboBox<Integer> loanTermCombo;

    // Financial Info
    @FXML private RadioButton existingLoansYes, existingLoansNo;
    @FXML private VBox existingLoansFields;
    @FXML private TextField totalOutstandingAmount, monthlyInstallments, creditScore;

    // POLYMORPHISM: This method implements the abstract initialize method from Initializable interface
    // This is an example of method overriding (runtime polymorphism)
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateLoanTermCombo();
        setupRadioButtons();
    }

    private void populateLoanTermCombo() {
        for (int i = 1; i <= 30; i++) {
            loanTermCombo.getItems().add(i);
        }
        loanTermCombo.setPromptText("Select loan term in years");
    }

    private void setupRadioButtons() {
        javafx.scene.control.ToggleGroup existingLoansGroup = new javafx.scene.control.ToggleGroup();
        existingLoansYes.setToggleGroup(existingLoansGroup);
        existingLoansNo.setToggleGroup(existingLoansGroup);

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
    private void handleNextButton(ActionEvent event) {
        if (!validateForm()) {
            showValidationAlert();
            return;
        }

        saveFormData();
        

        //polimorphism:......................................
        LoanEvaluator evaluator = new LoanEvaluator();
        LoanApprovalResult approvalResult = evaluator.evaluate(applicationData);

        System.out.println("=== Approval Result ===");
        System.out.println("Approved: " + approvalResult.isApproved());
        System.out.println("Reason: " + approvalResult.getReasons());
        System.out.println("Final Risk Score: " + approvalResult.getRiskScore());

        try {
            FXMLLoader loader;
            Parent root;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (approvalResult.isApproved()) {
                // Load appro.fxml from classpath root
                loader = new FXMLLoader(getClass().getResource("/appro.fxml"));
                root = loader.load();

                // Controller for approved result view
                com.controller.resultController controller = loader.getController();
                controller.setApplicationData(
                        applicationData.getFullName(),
                        applicationData.getNic(),
                        applicationData.getApplicationDate(),
                        applicationData.getRequestedLoanAmount(),
                        approvalResult.getRiskScore(),
                        applicationData.getLoanTermYears()
                );
                controller.setNicNumber(applicationData.getNic());

                // Persist full application
                boolean saved = DatabaseHelper.saveApplication(applicationData, approvalResult.getRiskScore(), null);
                System.out.println("Application saved to database: " + saved);

            } else {
                // Load reject.fxml from classpath root
                loader = new FXMLLoader(getClass().getResource("/reject.fxml"));
                root = loader.load();

                // Controller for rejection view
                com.controller.rejectController controller = loader.getController();
                controller.setApplicationData(
                        applicationData.getFullName(),
                        applicationData.getNic(),
                        applicationData.getApplicationDate(),
                        applicationData.getRequestedLoanAmount(),
                        approvalResult.getRiskScore(),
                        approvalResult.getReasons()
                );
                controller.setNicNumber(applicationData.getNic());

                // Persist full application with rejection reasons
                boolean saved = DatabaseHelper.saveApplication(applicationData, approvalResult.getRiskScore(), approvalResult.getReasons());
                System.out.println("Application saved to database: " + saved);
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            System.out.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenSearch(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/search.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading search.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showValidationAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following errors:");
        alert.setContentText("• All required fields must be filled\n" +
                           "• Email must be in valid format (e.g., user@example.com)\n" +
                           "• Phone number must contain only numbers and valid characters\n" +
                           "• Monthly Income, Loan Amount, Employment Duration, Outstanding Amount, and Monthly Installments must be valid integers\n" +
                           "• Credit Score must be a valid number");
        alert.showAndWait();
    }

    private boolean validateForm() {
        boolean valid = true;

        valid &= validateField(fullNameField, "Full Name");
        valid &= validateField(nicField, "NIC Number");

        valid &= validateField(employmentDurationField, "Employment Duration");
        valid &= validateField(monthlyIncomeField, "Monthly Income");

        valid &= validateField(requestedLoanAmountField, "Requested Loan Amount");
        valid &= validateComboBox(loanTermCombo, "Loan Term");

        valid &= validateField(creditScore, "Credit Score");

        if (existingLoansYes.isSelected()) {
            valid &= validateField(totalOutstandingAmount, "Total Outstanding Amount");
            valid &= validateField(monthlyInstallments, "Monthly Installments");
        }

        return valid;
    }

    private boolean validateField(TextField field, String fieldName) {
        if (field == null || field.getText() == null || field.getText().trim().isEmpty()) {
            if (field != null) {
                field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
            }
            System.out.println("Validation failed: " + fieldName + " is required");
            return false;
        } else {
            String fieldValue = field.getText().trim();

            if (fieldName.equals("Email Address") && !isValidEmail(fieldValue)) {
                field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
                System.out.println("Validation failed: " + fieldName + " format is invalid");
                return false;
            }

            if (fieldName.equals("Phone Number") && !isValidPhoneNumber(fieldValue)) {
                field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
                System.out.println("Validation failed: " + fieldName + " must contain only numbers");
                return false;
            }

            if (isNumericField(fieldName) && !isValidInteger(fieldValue)) {
                field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
                System.out.println("Validation failed: " + fieldName + " must be a valid integer");
                return false;
            }

            field.setStyle("");
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String phoneRegex = "^[+]?[\\d\\s\\-()]+$";
        return phone.matches(phoneRegex);
    }

    private boolean isValidInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            String cleanValue = value.replaceAll("[LKR\\s,]", "");
            Integer.parseInt(cleanValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isNumericField(String fieldName) {
        return fieldName.equals("Monthly Income") ||
               fieldName.equals("Requested Loan Amount") ||
               fieldName.equals("Employment Duration") ||
               fieldName.equals("Total Outstanding Amount") ||
               fieldName.equals("Monthly Installments");
    }

    private boolean validateComboBox(ComboBox<?> comboBox, String fieldName) {
        if (comboBox == null || comboBox.getValue() == null) {
            if (comboBox != null) {
                comboBox.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2;");
            }
            System.out.println("Validation failed: " + fieldName + " is required");
            return false;
        } else {
            comboBox.setStyle("");
            return true;
        }
    }

    private void saveFormData() {
        applicationData.setFullName(fullNameField.getText().trim());
        applicationData.setNic(nicField.getText().trim());  // NIC stored here
        applicationData.setPhone("");
        applicationData.setEmail("");

        applicationData.setAddress("");
        applicationData.setEmploymentType("General");
        applicationData.setJobTitle("General Position");

        try {
            String durationText = employmentDurationField.getText().trim();
            int duration = parseEmploymentDuration(durationText);
            applicationData.setEmploymentDuration(duration);
        } catch (NumberFormatException e) {
            System.out.println("Invalid employment duration format: " + employmentDurationField.getText());
            applicationData.setEmploymentDuration(0);
        }

        try {
            String incomeText = monthlyIncomeField.getText().trim()
                    .replace("LKR", "").replace(",", "").trim();
            applicationData.setMonthlyIncome(Double.parseDouble(incomeText));
        } catch (NumberFormatException e) {
            System.out.println("Invalid monthly income format: " + monthlyIncomeField.getText());
            applicationData.setMonthlyIncome(0.0);
        }

        try {
            String loanAmountText = requestedLoanAmountField.getText().trim()
                    .replace("LKR", "").replace(",", "").trim();
            applicationData.setRequestedLoanAmount(Double.parseDouble(loanAmountText));
        } catch (NumberFormatException e) {
            System.out.println("Invalid loan amount format: " + requestedLoanAmountField.getText());
            applicationData.setRequestedLoanAmount(0.0);
        }

        applicationData.setLoanType("General Loan");
        applicationData.setLoanTermYears(loanTermCombo.getValue());

        applicationData.setHasExistingLoans(existingLoansYes.isSelected());

        if (applicationData.hasExistingLoans()) {
            try {
                String outstandingText = totalOutstandingAmount.getText().trim()
                        .replace("LKR", "").replace(",", "").trim();
                applicationData.setTotalOutstandingLoan(Double.parseDouble(outstandingText));
            } catch (NumberFormatException e) {
                System.out.println("Invalid outstanding amount format: " + totalOutstandingAmount.getText());
                applicationData.setTotalOutstandingLoan(0.0);
            }

            try {
                String installmentText = monthlyInstallments.getText().trim()
                        .replace("LKR", "").replace(",", "").trim();
                applicationData.setMonthlyInstallments(Double.parseDouble(installmentText));
            } catch (NumberFormatException e) {
                System.out.println("Invalid monthly installment format: " + monthlyInstallments.getText());
                applicationData.setMonthlyInstallments(0.0);
            }
        } else {
            applicationData.setTotalOutstandingLoan(0.0);
            applicationData.setMonthlyInstallments(0.0);
        }

        try {
            applicationData.setCreditScore(Integer.parseInt(creditScore.getText().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit score format: " + creditScore.getText());
            applicationData.setCreditScore(0);
        }

        applicationData.setApplicationDate(LocalDate.now().toString());
        applicationData.setApplicationStatus("SUBMITTED");

        System.out.println("=== Form Saved ===");
        System.out.println("Applicant: " + applicationData.getFullName());
        System.out.println("NIC: " + applicationData.getNic());
        System.out.println("Phone: " + applicationData.getPhone());
        System.out.println("Email: " + applicationData.getEmail());
        System.out.println("Employment Duration: " + applicationData.getEmploymentDuration() + " months");
        System.out.println("Monthly Income: LKR " + applicationData.getMonthlyIncome());
        System.out.println("Requested Loan Amount: LKR " + applicationData.getRequestedLoanAmount());
        System.out.println("Loan Term: " + applicationData.getLoanTermYears() + " years");
        System.out.println("Has Existing Loans: " + applicationData.hasExistingLoans());
        if (applicationData.hasExistingLoans()) {
            System.out.println("Outstanding Loan: LKR " + applicationData.getTotalOutstandingLoan());
            System.out.println("Monthly Installments: LKR " + applicationData.getMonthlyInstallments());
        }
        System.out.println("Credit Score: " + applicationData.getCreditScore());
    }

    private int parseEmploymentDuration(String durationText) {
        if (durationText == null || durationText.isEmpty()) {
            throw new NumberFormatException("Empty employment duration");
        }
        durationText = durationText.toLowerCase().trim();
        int years = 0;
        java.util.regex.Matcher yearMatcher = java.util.regex.Pattern.compile("^(\\d+)\\s*(year|years)?$").matcher(durationText);
        if (yearMatcher.matches()) {
            years = Integer.parseInt(yearMatcher.group(1));
        } else {
            throw new NumberFormatException("Employment duration must be in years only (e.g., '2' or '2 years')");
        }
        return years * 12;
    }
}
