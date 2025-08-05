package com.loanshield;

public class LoanApplicationData {
    private String fullName;
    private String nic;
    private String phone;
    private String email;
    private String address;

    private String employmentType;
    private String jobTitle;
    private int employmentDuration;
    private double monthlyIncome;

    private double requestedLoanAmount;
    private String loanType;
    private int loanTermYears;

    private boolean hasExistingLoans;
    private double totalOutstandingLoan;
    private double monthlyInstallments;
    private int creditScore;

    private String applicationStatus = "DRAFT";
    private String applicationDate;
    private String applicationId;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public int getEmploymentDuration() { return employmentDuration; }
    public void setEmploymentDuration(int employmentDuration) { this.employmentDuration = employmentDuration; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public double getRequestedLoanAmount() { return requestedLoanAmount; }
    public void setRequestedLoanAmount(double requestedLoanAmount) { this.requestedLoanAmount = requestedLoanAmount; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public int getLoanTermYears() { return loanTermYears; }
    public void setLoanTermYears(int loanTermYears) { this.loanTermYears = loanTermYears; }

    public boolean hasExistingLoans() { return hasExistingLoans; }
    public void setHasExistingLoans(boolean hasExistingLoans) { this.hasExistingLoans = hasExistingLoans; }

    public double getTotalOutstandingLoan() { return totalOutstandingLoan; }
    public void setTotalOutstandingLoan(double totalOutstandingLoan) { this.totalOutstandingLoan = totalOutstandingLoan; }

    public double getMonthlyInstallments() { return monthlyInstallments; }
    public void setMonthlyInstallments(double monthlyInstallments) { this.monthlyInstallments = monthlyInstallments; }

    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public String getApplicationDate() { return applicationDate; }
    public void setApplicationDate(String applicationDate) { this.applicationDate = applicationDate; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
}
