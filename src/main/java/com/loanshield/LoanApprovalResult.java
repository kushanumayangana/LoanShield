package com.loanshield;

public class LoanApprovalResult {
    private final boolean approved;
    private final String reason;
    private final int riskScore;

    public LoanApprovalResult(boolean approved, String reason, int riskScore) {
        this.approved = approved;
        this.reason = reason;
        this.riskScore = riskScore;
    }

    public boolean isApproved() { return approved; }
    public String getReason() { return reason; }
    public int getRiskScore() { return riskScore; }

    @Override
    public String toString() {
        return "LoanApprovalResult{" +
                "approved=" + approved +
                ", reason='" + reason + '\'' +
                ", riskScore=" + riskScore +
                '}';
    }
}
