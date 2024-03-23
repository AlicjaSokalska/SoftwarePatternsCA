package com.example.softwarepatternsca;
public class Product {
    private String id;
    private String name;
    private String manufacturer;
    private double price;
    private String category;
    private String imageUri; // Add this field for image URI
    private int stockLevel; // New attribute for stock level
    private int reviews;

    public Product() {
        // Default constructor required for Firebase
    }

    // Constructor with stock level
    public Product(String id, String name, String manufacturer, double price, String category, String imageUri, int stockLevel, int reviews) {
        this.id = id;
        this.name = name;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
        this.imageUri = imageUri;
        this.stockLevel = stockLevel;
        this.reviews = reviews;
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
    }}