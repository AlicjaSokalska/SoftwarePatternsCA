package com.example.softwarepatternsca;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class Product implements ProductSubject {
    private static final int LOW_STOCK_THRESHOLD =10 ;
    private String id;
    private String name;
    private String manufacturer;
    private double price;
    private String category;
    private String imageUri;
    private String comments;
    private int stockLevel;
    private int reviews;
    private List<ProductObserver> observers = new ArrayList<>();


    // Constructor
    public Product(String id, String name, String manufacturer, double price, String category, String imageUri, int stockLevel, int reviews, String comments) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
        this.imageUri = imageUri;
        this.stockLevel = stockLevel;
        this.reviews = reviews;
        this.comments = comments;
        this.observers = new ArrayList<>();
    }

    public Product() {
        // Default constructor required for Firebase
    }


    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;

    }


    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
        Log.d("Product", "Stock level set for product: " + name);
        notifyObservers(this);
    }


    @Override
    public void registerObserver(ProductObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ProductObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Product product) {
        if (observers != null && stockLevel <= LOW_STOCK_THRESHOLD) { // Assuming LOW_STOCK_THRESHOLD is a constant indicating the threshold for low stock
            for (ProductObserver observer : observers) {
                observer.notifyLowStock(product);
            }
        }
    }
}