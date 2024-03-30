package com.example.softwarepatternsca;

public class Admin implements ProductObserver {
    private String name;

    public Admin(String name) {
        this.name = name;
    }


    @Override
    public void notifyLowStock(Product product) {
        // Notify admin about low stock
        System.out.println("Admin " + name + " received notification: Product " + product.getName() + " is out of stock.");
    }


    // Other admin-related methods
}
