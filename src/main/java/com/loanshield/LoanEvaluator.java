package com.loanshield;

import java.util.List;

// ABSTRACTION


public class LoanEvaluator {


    public LoanApprovalResult evaluate(LoanApplicationData app) {
        double monthlyIncome = app.getMonthlyIncome();
        double monthlyInstallments = app.getMonthlyInstallments();
        int creditScore = app.getCreditScore();
        double requestedLoanAmount = app.getRequestedLoanAmount();
        int employmentDuration = app.getEmploymentDuration(); // fixed from String to int


        if (monthlyIncome <= 0) {
            return new LoanApprovalResult(false, List.of("Monthly income must be greater than zero"), 0);
        }

        double dti = monthlyInstallments / monthlyIncome;
        double annualIncome = monthlyIncome * 12;
        double loanToIncome = requestedLoanAmount / annualIncome;

        int riskScore = 0;
        java.util.List<String> reasons = new java.util.ArrayList<>();

        // ABSTRACTION: Complex business rules are encapsulated within this method
        // --- Debt-to-Income ---
        if (dti < 0.3) {
            riskScore += 25;
        } else if (dti < 0.4) {
            riskScore += 15;
        } else {
            reasons.add("Too much existing debt (DTI > 40%)");
        }

        // --- Credit Score ---
        if (creditScore >= 750) {
            riskScore += 25;
        } else if (creditScore >= 650) {
            riskScore += 15;
        } else {
            reasons.add("Low credit score (CRIB < 650)");
        }

        // --- Employment Stability ---
        if (employmentDuration >= 24) {
            riskScore += 25;
        } else if (employmentDuration >= 12) {
            riskScore += 15;
        } else {
            reasons.add("Insufficient employment history (< 1 year)");
        }

        // --- Loan-to-Income Ratio ---
        if (loanToIncome <= 4.0) {
            riskScore += 25;
        } else if (loanToIncome <= 5.0) {
            riskScore += 15;
        } else {
            reasons.add("Requested loan exceeds affordability");
        }

        boolean approved = reasons.isEmpty() && riskScore >= 65;
        if (approved) {
            reasons.add("Approved");
        } else if (reasons.isEmpty()) {
            reasons.add("Rejected due to low risk score");
        }

        return new LoanApprovalResult(approved, reasons, riskScore);
    }
}
