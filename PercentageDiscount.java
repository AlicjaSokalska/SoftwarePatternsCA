package com.example.softwarepatternsca;

public class PercentageDiscount implements Discount {
    private double percentage;

    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }
    public double getPercentage() {
        return percentage;
    }
    @Override
    public double applyDiscount(double totalAmount) {
        return totalAmount * (1 - percentage / 100);
    }
}
