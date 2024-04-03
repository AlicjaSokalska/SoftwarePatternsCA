package com.example.softwarepatternsca;

// Define a concrete visitor implementation for admin transactions
class AdminTransactionVisitor implements TransactionVisitor {
    @Override
    public void visit(Transaction transaction) {

        System.out.println("Transaction Details: " + transaction);
    }
}
