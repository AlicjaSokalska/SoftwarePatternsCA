package com.example.softwarepatternsca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemClickListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartManager.CartItem> cartItems;
    private TextView totalAmountTextView;
    private Button checkoutButton;
    private double totalAmount = 0;

    private Stack<ShoppingCartMemento> mementoStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_recycler_view);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        checkoutButton = findViewById(R.id.btnCheckout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems, this); // Pass this activity as the listener
        recyclerView.setAdapter(cartAdapter);

        // Load cart items
        loadCartItems();
        setUpUndoButton();

        // Set up checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to checkout activity
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });
    }



    private void restorePreviousState() {
        if (!mementoStack.isEmpty()) {
            ShoppingCartMemento previousState = mementoStack.pop();
            cartItems.clear();
            cartItems.addAll(previousState.getItems());
            cartAdapter.notifyDataSetChanged();
            updateTotalAmount();
        } else {
            // No more previous states to restore
            Toast.makeText(this, "Cannot undo further", Toast.LENGTH_SHORT).show();
        }}
        private void setUpUndoButton() {
            Button undoButton = findViewById(R.id.btnUndo);
            undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restorePreviousState();
                }
            });
        }


    private void loadCartItems() {
        // Get the current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Create a reference to the user's cart node
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cartItems.clear();
                    totalAmount = 0; // Reset total amount
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String productId = snapshot.getKey();
                        String productName = snapshot.child("name").getValue(String.class);
                        double productPrice = snapshot.child("price").getValue(Double.class);
                        int quantity = snapshot.child("quantity").getValue(Integer.class);

                        Product product = new Product(productId, productName, "", productPrice, "", "", quantity, 0,"");
                        cartItems.add(new CartManager.CartItem(product, quantity));
                        totalAmount += productPrice * quantity; // Calculate total amount
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                    applyDiscountDecorator();
                    applyDiscount(totalAmount);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
        ShoppingCartMemento memento = new ShoppingCartMemento(cartItems);
        mementoStack.push(memento);
    }

    // Method to update total amount TextView
    private void updateTotalAmount() {
        totalAmount = 0; // Reset total amount
        for (CartManager.CartItem item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
        totalAmountTextView.setText("Total: $" + String.format("%.2f", totalAmount));
        // Pass total amount to CheckoutActivity
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to checkout activity
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("totalAmount", totalAmount);
                startActivity(intent);
            }
        });
    }



    // Implementing CartItemClickListener interface method
    public void onCartItemClicked(int position) {
        // Remove the item from the list
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);

        // Recalculate and update total amount
        updateTotalAmount();

        ShoppingCartMemento memento = new ShoppingCartMemento(cartItems);
        mementoStack.push(memento);
    }

    private double applyDiscount(double totalAmount) {
        // Create discount using factory
        Discount discount = DiscountFactory.createDiscount(totalAmount);
        // Apply discount
        double discountedAmount = discount.applyDiscount(totalAmount);

        // Notify user about the discount
        if (discount instanceof PercentageDiscount) {
            double discountPercentage = ((PercentageDiscount) discount).getPercentage();
            Toast.makeText(CartActivity.this, "Congratulations! You received a " + discountPercentage + "% discount.", Toast.LENGTH_SHORT).show();
        } else if (discount instanceof NoDiscount) {
            Toast.makeText(CartActivity.this, "No discount applied.", Toast.LENGTH_SHORT).show();
        }

        return discountedAmount;
    }

    private void applyDiscountDecorator() {
        if (totalAmount > 200) {
            // Apply discount decorator
            List<CartManager.CartItem> discountedCartItems = new ArrayList<>();
            for (CartManager.CartItem cartItem : cartItems) {
                // Calculate discounted price
                double originalPrice = cartItem.getProduct().getPrice();
                double discountedPrice = originalPrice * 0.2; // 10% discount

                // Create a new product with discounted price
                Product discountedProduct = new Product(
                        cartItem.getProduct().getId(),
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getManufacturer(),
                        discountedPrice,
                        cartItem.getProduct().getCategory(),
                        cartItem.getProduct().getImageUri(),
                        cartItem.getProduct().getStockLevel(),
                        cartItem.getProduct().getReviews(),
                        cartItem.getProduct().getComments()
                );
                discountedCartItems.add(new CartManager.CartItem(discountedProduct, cartItem.getQuantity()));
            }
            cartItems.clear();
            cartItems.addAll(discountedCartItems);
            cartAdapter.notifyDataSetChanged();

            // Notify user about the discount
            Toast.makeText(CartActivity.this, "Congratulations! You are eligible for a 20% discount.", Toast.LENGTH_SHORT).show();
        }

}
  /* private void applyDiscountDecorator() {
       if (totalAmount < 200) {
           // Apply delivery charge of $10
           totalAmount += 10;
           Toast.makeText(CartActivity.this, "Delivery charge of €10 applied.", Toast.LENGTH_SHORT).show();
       } else {
           // Free delivery for total amount over $200
           Toast.makeText(CartActivity.this, "Free delivery applied.", Toast.LENGTH_SHORT).show();
       }
       // Update the total amount TextView
       totalAmountTextView.setText("Total: €" + String.format("%.2f", totalAmount));
   }
*/


}