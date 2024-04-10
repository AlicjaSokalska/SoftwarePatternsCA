package com.example.softwarepatternsca;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartMemento {
    private List<CartManager.CartItem> items;

    public ShoppingCartMemento(List<CartManager.CartItem> items) {
        this.items = new ArrayList<>(items); // Create a deep copy of the items list
    }

    public List<CartManager.CartItem> getItems() {
        return items;
    }
}
