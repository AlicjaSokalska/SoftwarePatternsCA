package com.example.softwarepatternsca;

public class Transaction {

    private double totalAmount;
    private String paymentMethod;
    private String transactionNumber;

    public Transaction() {

    }


    public Transaction(double totalAmount, String paymentMethod, String transactionNumber) {
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionNumber = transactionNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }
    void accept(TransactionVisitor visitor) {
        visitor.visit(this);
    }
}
