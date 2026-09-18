package service;

/**
 * Simple immutable holder pairing a member with their accumulated fine.
 * Used for building the fine report / fine chart.
 */
public class FineRecord {
    private final String membershipId;
    private final String memberName;
    private final double amount;

    public FineRecord(String membershipId, String memberName, double amount) {
        this.membershipId = membershipId;
        this.memberName = memberName;
        this.amount = amount;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public String getMemberName() {
        return memberName;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | Fine: Rs. %.2f", membershipId, memberName, amount);
    }
}
