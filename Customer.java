package com.example.softwarepatternsca;

import java.util.ArrayList;
import java.util.List;

public class Customer implements ProductObserver {
    private String email;
    private String name;
    private String surname;
    private String address;
    private String paymentMethod;

    private List<Product> subscribedProducts;

    // Private constructor to prevent direct instantiation
    private Customer(Builder builder) {
        this.email = builder.email;
        this.name = builder.name;
        this.surname = builder.surname;
        this.address = builder.address;
        this.paymentMethod = builder.paymentMethod;
        this.subscribedProducts = new ArrayList<>();
    }

    // Builder class for constructing Customer objects
    public static class Builder {
        private String email;
        private String name;
        private String surname;
        private String address;
        private String paymentMethod;

        public Builder() {
            // Set default values if needed
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Method to subscribe to product notifications
    public void subscribeToProduct(Product product) {
        subscribedProducts.add(product);
    }

    // Method to unsubscribe from product notifications
    public void unsubscribeFromProduct(Product product) {
        subscribedProducts.remove(product);
    }

    // Update method to receive notifications about new products

    @Override
    public void notifyLowStock(Product product) {

    }



}
