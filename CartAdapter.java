package com.example.softwarepatternsca;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Stack;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartManager.CartItem> cartItems;
    private Context context;
    private CartItemClickListener listener;
    private DatabaseReference cartRef;


    public CartAdapter(List<CartManager.CartItem> cartItems, CartItemClickListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;

        // Initialize Firebase database reference
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            cartRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("cart");
        } else {
            // Handle the case where the user is not authenticated
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartManager.CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface CartItemClickListener {
        void onCartItemClicked(int position);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            itemView.setOnClickListener(this);
        }

        public void bind(CartManager.CartItem cartItem) {
            Product product = cartItem.getProduct();
            productNameTextView.setText("Name: " + product.getName());
            productPriceTextView.setText("Price: €" + product.getPrice());
            productQuantityTextView.setText("Quantity: " + cartItem.getQuantity());
        }
        @Override
        public void onClick(View v) {
            // Pass the clicked position to the listener
            if (listener != null) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose action")
                            .setItems(new CharSequence[]{"Remove", "Update Quantity"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // Remove
                                            // Remove item from Firebase and notify adapter
                                            String productId = cartItems.get(position).getProduct().getId();
                                            cartRef.child(productId).removeValue(); // Remove from Firebase
                                            cartItems.remove(position);
                                            notifyItemRemoved(position);
                                            break;
                                        case 1: // Update Quantity
                                            showUpdateQuantityDialog(position);
                                            break;
                                    }
                                }
                            });
                    builder.create().show();
                }
            }
        }
    }
    private void showUpdateQuantityDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Quantity");

        // Set up the input field
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newQuantityStr = input.getText().toString();
                if (!TextUtils.isEmpty(newQuantityStr)) {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    if (newQuantity > 0) {
                        // Update quantity in Firebase
                        String productId = cartItems.get(position).getProduct().getId();
                        cartRef.child(productId).child("quantity").setValue(newQuantity);
                        cartItems.get(position).setQuantity(newQuantity);
                        notifyItemChanged(position);
                    } else {
                        Toast.makeText(context, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}