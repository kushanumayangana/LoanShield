package com.loanshield;

import java.util.List;

// ENCAPSULATION: This class demonstrates encapsulation by:
// 1. Using final fields to make the object immutable after construction
// 2. Providing only getter methods (no setters) to prevent modification after creation
// 3. Controlling access to the internal state
public class LoanApprovalResult {
    // ENCAPSULATION: Final fields ensure immutability - values cannot be changed after object creation
    private final boolean approved;
    private final List<String> reasons;
    private final int riskScore;

    public LoanApprovalResult(boolean approved, List<String> reasons, int riskScore) {
        this.approved = approved;
        this.reasons = reasons;
        this.riskScore = riskScore;
    }

    // ENCAPSULATION: Only getter methods are provided - no setters to maintain immutability
    public boolean isApproved() { return approved; }
    public List<String> getReasons() { return reasons; }
    public int getRiskScore() { return riskScore; }

    // POLYMORPHISM: This method overrides the toString method from Object class
    // This demonstrates method overriding (runtime polymorphism)
    @Override
    public String toString() {
        return "LoanApprovalResult{" +
                "approved=" + approved +
                ", reasons=" + reasons +
                ", riskScore=" + riskScore +
                '}';
    }
}
