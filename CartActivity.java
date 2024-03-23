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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemClickListener {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartManager.CartItem> cartItems;
    private TextView totalAmountTextView;
    private Button checkoutButton;
    private double totalAmount = 0;

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

        // Set up checkout button click listener
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to checkout activity
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
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

                        Product product = new Product(productId, productName, "", productPrice, "", "", quantity, 0);
                        cartItems.add(new CartManager.CartItem(product, quantity));
                        totalAmount += productPrice * quantity; // Calculate total amount
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalAmount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    // Method to update total amount TextView
    private void updateTotalAmount() {
        totalAmount = 0; // Reset total amount
        for (CartManager.CartItem item : cartItems) {
            totalAmount += item.getProduct().getPrice() * item.getQuantity();
        }
        totalAmountTextView.setText("Total: $" + String.format("%.2f", totalAmount));
    }

    // Implementing CartItemClickListener interface method
    public void onCartItemClicked(int position) {
        // Remove the item from the list
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);

        // Recalculate and update total amount
        updateTotalAmount();
    }
}