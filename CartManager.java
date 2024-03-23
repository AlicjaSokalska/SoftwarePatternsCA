package com.example.softwarepatternsca;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static List<CartItem> cartItems = new ArrayList<>();

    public static void addToCart(Product product, int quantity) {
        // Check if the product is already in the cart
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                // If yes, update the quantity
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // If the product is not in the cart, add it as a new item
        cartItems.add(new CartItem(product, quantity));
    }

    // Other methods for managing the cart, such as removing items, getting total price, etc.

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
