package com.example.softwarepatternsca;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;
    private List<CartManager.CartItem> cartItems;
    private TextView totalAmountTextView;
    private EditText nameEditText, addressEditText, contactEditText,surnameEditText;
    private EditText cardNumberEditText, cardNameEditText, cvvEditText;
    private String paymentMethod;
    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;
    private double totalAmount = 0;
    private Spinner paymentMethodSpinner; // Declare paymentMethodSpinner as a class field
    private ArrayAdapter<CharSequence> adapter; // Declare adapter as a class field


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.checkoutRecyclerView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        contactEditText = findViewById(R.id.contactEditText);
        // Initialize other views as needed

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItems = new ArrayList<>();
        checkoutAdapter = new CheckoutAdapter(cartItems);
        recyclerView.setAdapter(checkoutAdapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("totalAmount")) {
            totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
            totalAmountTextView.setText("Total Amount: €" + String.format("%.2f", totalAmount));
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("customer_details").child("name").getValue(String.class);
                        String surname = dataSnapshot.child("customer_details").child("surname").getValue(String.class);
                        String address = dataSnapshot.child("customer_details").child("address").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        // Set retrieved values to EditText fields
                        nameEditText.setText(name + " " + surname);
                        surnameEditText.setText(surname);
                        addressEditText.setText(address);
                        contactEditText.setText(email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }

        // Implement checkout button click listener
        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Process checkout: Update stock levels, display receipt, etc.
                processCheckout();
            }
        });


        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // Autofill spinner with user's selected payment method
        getUserPaymentMethod();

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paymentMethod = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void getUserPaymentMethod() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String paymentMethod = dataSnapshot.child("paymentMethod").getValue(String.class);
                        if (paymentMethod != null) {
                            // Set the payment method to the spinner
                            int position = adapter.getPosition(paymentMethod);
                            if (position != -1) {
                                paymentMethodSpinner.setSelection(position);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void processCheckout() {
        switch (paymentMethod) {
            case "Credit Card":
            case "Debit Card":
                showCardDetailsDialog();
                break;
            case "PayPal":
                showPayPalDialog();
                break;
            case "Bank Transfer":
                showBankTransferDialog();
                break;
            default:
                // Handle other payment methods or continue checkout without payment details
                break;
        }
    }
    private void showPayPalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("PayPal Login");

        // Inflate the layout for the dialog
        View view = getLayoutInflater().inflate(R.layout.dialog_paypal, null);
        builder.setView(view);

        EditText usernameEditText = view.findViewById(R.id.usernameEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);

        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                navigateToReceiptActivity();
                // You can perform further actions with the username and password, like authentication
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void showBankTransferDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bank Transfer Details");

        // Inflate the layout for the dialog
        View view = getLayoutInflater().inflate(R.layout.dialog_bank_transfer, null);
        builder.setView(view);

        EditText accountNumberEditText = view.findViewById(R.id.accountNumberEditText);
        EditText routingNumberEditText = view.findViewById(R.id.routingNumberEditText);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String accountNumber = accountNumberEditText.getText().toString().trim();
                String routingNumber = routingNumberEditText.getText().toString().trim();
                navigateToReceiptActivity();
                // You can perform further actions with the account and routing numbers
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void showCardDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Card Details");

        View view = getLayoutInflater().inflate(R.layout.dialog_card_details, null);
        builder.setView(view);

        cardNumberEditText = view.findViewById(R.id.cardNumberEditText);
        cardNameEditText = view.findViewById(R.id.cardNameEditText);
        cvvEditText = view.findViewById(R.id.cvvEditText);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cardNumber = cardNumberEditText.getText().toString();
                String cardName = cardNameEditText.getText().toString();
                String cvv = cvvEditText.getText().toString();
                navigateToReceiptActivity();
                // Use card details for payment processing
                // Update stock levels in Firebase, display receipt, etc.
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }


    private void navigateToReceiptActivity() {
        // Generate a transaction number
        String transactionNumber = generateTransactionNumber();
        updateStock();
        // Start the receipt activity
        Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("paymentMethod", paymentMethod);
        intent.putExtra("transactionNumber", transactionNumber);

       // deleteCartData();

        startActivity(intent);
        finish(); // Optional: Finish the current activity if you don't need to go back to it
    }
    private void updateStock() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                            String productId = cartItemSnapshot.getKey();
                            int quantity = cartItemSnapshot.child("quantity").getValue(Integer.class);
                            updateProductStock(productId, quantity);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private void updateProductStock(String productId, final int quantity) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentStock = dataSnapshot.child("stockLevel").getValue(Integer.class);
                    int updatedStock = currentStock - quantity;
                    // Ensure stock doesn't go below zero
                    updatedStock = Math.max(updatedStock, 0);
                    // Update the stock level in the database
                    dataSnapshot.getRef().child("stockLevel").setValue(updatedStock).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Stock updated successfully
                                Log.d("CheckoutActivity", "Stock for product " + productId + " updated successfully");
                                deleteCartData();
                            } else {
                                // Failed to update stock
                                Log.d("CheckoutActivity", "Failed to update stock for product " + productId + ": " + task.getException().getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    private void deleteCartData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");
            cartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Cart data deleted successfully
                        Log.d("CheckoutActivity", "User cart data deleted successfully");
                    } else {
                        // Failed to delete cart data
                        Log.d("CheckoutActivity", "Failed to delete user cart data: " + task.getException().getMessage());
                    }
                }
            });
        }
    }


    private String generateTransactionNumber() {
        // Generate a unique transaction number here, for example, you can use a combination of current time and user ID
        // Here's a simple example, you can customize it as per your requirements
        return "TXN-" + System.currentTimeMillis();
    }


}


