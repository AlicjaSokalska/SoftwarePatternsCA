package com.example.softwarepatternsca;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ProductManager {
    private static ProductManager instance;
    private List<ProductObserver> observers;
    private List<Product> productList;

    private ProductManager() {
        observers = new ArrayList<>();
        productList = new ArrayList<>();
    }

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        productList.add(product);

    }

    public void registerObserver(ProductObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ProductObserver observer) {
        observers.remove(observer);
    }

    // Method to check stock levels and notify admin
   /* public void checkStockLevels() {
        Log.d("ProductManager", "Checking stock levels...");
        for (Product product : productList) {
            if (product.getStockLevel() <= 10 && product.getStockLevel() > 0) {
                notifyObserversLowStock(product);
            }
        }
    }*/
    public void checkStockLevels() {
    Log.d("ProductManager", "Checking stock levels...");
    for (Product product : productList) {
        int stockLevel = product.getStockLevel();
        if (stockLevel <= 10 && stockLevel > 0) {
            notifyObserversLowStock(product);
        } else if (stockLevel == 0) {
            notifyObserversLowStock(product); // You might want to handle this case differently
        }
    }
}


    public void notifyObserversLowStock(Product product) {
        Log.d("ProductManager", "Notifying observers of low stock for product: " + product.getName());
        for (ProductObserver observer : observers) {
            observer.notifyLowStock(product);
        }
    }
}
