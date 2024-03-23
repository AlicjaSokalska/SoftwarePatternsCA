package com.example.softwarepatternsca;
public class Customer {
    private String email;
    private String name;
    private String surname;
    private String address;
    private String paymentMethod;

    // Default constructor (required for Firebase)
    public Customer() {
    }

    public Customer(String email, String name, String surname, String address, String paymentMethod) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.paymentMethod = paymentMethod;
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
}
