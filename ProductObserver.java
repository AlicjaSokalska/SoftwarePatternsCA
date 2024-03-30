package com.example.softwarepatternsca;

public interface ProductObserver {

    void notifyLowStock(Product product);

}

// Subject interface
interface ProductSubject {
    void registerObserver(ProductObserver observer);
    void removeObserver(ProductObserver observer);
    void notifyObservers(Product product);
}