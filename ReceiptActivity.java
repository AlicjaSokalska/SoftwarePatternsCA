package com.example.softwarepatternsca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReceiptActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Retrieve current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get user email from Firebase Realtime Database
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userEmail = dataSnapshot.child("email").getValue(String.class);

                        // Display user email
                        TextView userEmailTextView = findViewById(R.id.userEmailTextView);
                        userEmailTextView.setText("User Email: " + userEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
        Intent intent = getIntent();
        if (intent != null) {
            double totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
            String paymentMethod = intent.getStringExtra("paymentMethod");
            String transactionNumber = intent.getStringExtra("transactionNumber"); // Retrieve transaction number

            // Display total amount, payment method, and transaction number
            TextView totalAmountTextView = findViewById(R.id.totalAmountTextView);
            totalAmountTextView.setText("Total Amount: €" + totalAmount);

            TextView paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
            paymentMethodTextView.setText("Payment Method: " + paymentMethod);

            TextView transactionNumberTextView = findViewById(R.id.transactionNumberTextView); // TextView to display transaction number
            transactionNumberTextView.setText("Transaction Number: " + transactionNumber); // Set transaction number
            saveTransactionDetails(totalAmount, paymentMethod, transactionNumber);
        }

    }

    private void saveTransactionDetails(double totalAmount, String paymentMethod, String transactionNumber) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userTransactionsRef = mDatabase.child("users").child(userId).child("transactions").push();
            userTransactionsRef.child("totalAmount").setValue(totalAmount);
            userTransactionsRef.child("paymentMethod").setValue(paymentMethod);
            userTransactionsRef.child("transactionNumber").setValue(transactionNumber)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ReceiptActivity", "Transaction details saved successfully");
                                showToast("Transaction details saved successfully");
                            } else {
                                Log.e("ReceiptActivity", "Failed to save transaction details: " + task.getException().getMessage());
                                showToast("Failed to save transaction details");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("ReceiptActivity", "Error saving transaction details: " + e.getMessage());
                            showToast("Error saving transaction details");
                        }
                    });
        } else {
            Log.e("ReceiptActivity", "Current user is null, cannot save transaction details");
            showToast("User not authenticated");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}