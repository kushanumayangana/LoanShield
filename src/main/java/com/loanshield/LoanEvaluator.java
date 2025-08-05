package com.loanshield;

public class LoanEvaluator {

    public LoanApprovalResult evaluate(LoanApplicationData app) {
        double monthlyIncome = app.getMonthlyIncome();
        double monthlyInstallments = app.getMonthlyInstallments();
        int creditScore = app.getCreditScore();
        double requestedLoanAmount = app.getRequestedLoanAmount();
        int employmentDuration = app.getEmploymentDuration(); // fixed from String to int

        // Prevent division by zero
        if (monthlyIncome <= 0) {
            return new LoanApprovalResult(false, "Monthly income must be greater than zero", 0);
        }

        double dti = monthlyInstallments / monthlyIncome;
        double annualIncome = monthlyIncome * 12;
        double loanToIncome = requestedLoanAmount / annualIncome;

        int riskScore = 0;

        // --- Debt-to-Income ---
        if (dti < 0.3) {
            riskScore += 25;
        } else if (dti < 0.4) {
            riskScore += 15;
        } else {
            return new LoanApprovalResult(false, "Too much existing debt (DTI > 40%)", riskScore);
        }

        // --- Credit Score ---
        if (creditScore >= 750) {
            riskScore += 25;
        } else if (creditScore >= 650) {
            riskScore += 15;
        } else {
            return new LoanApprovalResult(false, "Low credit score (CRIB < 650)", riskScore);
        }

        // --- Employment Stability ---
        if (employmentDuration >= 24) {
            riskScore += 25;
        } else if (employmentDuration >= 12) {
            riskScore += 15;
        } else {
            return new LoanApprovalResult(false, "Insufficient employment history (< 1 year)", riskScore);
        }

        // --- Loan-to-Income Ratio ---
        if (loanToIncome <= 4.0) {
            riskScore += 25;
        } else if (loanToIncome <= 5.0) {
            riskScore += 15;
        } else {
            return new LoanApprovalResult(false, "Requested loan exceeds affordability", riskScore);
        }

        boolean approved = riskScore >= 50;
        String reason = approved ? "Approved" : "Rejected due to low risk score";

        return new LoanApprovalResult(approved, reason, riskScore);
    }

}
