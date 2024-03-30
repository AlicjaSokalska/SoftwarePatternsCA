package com.example.softwarepatternsca;

public class DiscountFactory {

    public static Discount createDiscount(double totalAmount) {
        if (totalAmount > 100) {
            return new PercentageDiscount(10); // Apply 10% discount if total amount exceeds 100
        } else {
            return new NoDiscount(); // No discount applied otherwise
        }
    }
}
