package com.loanshield;

public class LoanApplicationData {
    // Step 1: Personal Info
    public static String fullName;
    public static String nic;
    public static String mobile;
    public static String email;
    public static String address;

    // Step 2: Employment Info
    public static String employmentType;
    public static String jobTitle;
    public static String employer;
    public static String startDate;
    public static double monthlyIncome;

    // Step 3: Financial Info
    public static boolean hasExistingLoans;
    public static double loanAmountExisting;
    public static double monthlyPayments;
    public static int creditScore;

    // Step 4: Loan Request Info
    public static String loanPurpose;
    public static double requestedLoanAmount;
    public static int loanTerm;
}
