package com.example.softwarepatternsca;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationState implements AuthState {
    @Override
    public void authenticate(FirebaseAuth mAuth, String email, String password, Activity activity) {
        // Implement registration logic
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            currentUserRef.child("email").setValue(email)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("RegisterActivity", "User details saved successfully");
                                            // Redirect to the main activity
                                            Intent intent = new Intent(activity,AddCustomerDetails.class);
                                            activity.startActivity(intent);
                                            activity.finish(); // Close the registration activity to prevent going back
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("RegisterActivity", "Failed to save user details: " + e.getMessage());
                                            Toast.makeText(activity, "Failed to save user details", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Registration failed
                            Log.e("RegisterActivity", "User registration failed: " + task.getException().getMessage());
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
