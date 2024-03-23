package com.example.softwarepatternsca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCustomerDetails extends AppCompatActivity {

    private EditText nameEditText, surnameEditText, addressEditText;
    private Spinner paymentMethodSpinner;
    private Button saveButton,mainButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_details);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        TextView userEmailTextView = findViewById(R.id.user_email_text_view);
        nameEditText = findViewById(R.id.name_edit_text);
        surnameEditText = findViewById(R.id.surname_edit_text);
        addressEditText = findViewById(R.id.address_edit_text);
        paymentMethodSpinner = findViewById(R.id.payment_method_spinner);
        saveButton = findViewById(R.id.save_button);
        mainButton = findViewById(R.id.main_button);

        // Set user email on top
        if (currentUser != null) {
            userEmailTextView.setText("Welcome, " + currentUser.getEmail());
        }

        // Set up payment method spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_methods_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // Set click listener for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCustomerDetails();
            }
        });
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to the main activity
                Intent intent = new Intent(AddCustomerDetails.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });


    }

    private void saveCustomerDetails() {
        // Get user input
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();

        // Validate user input
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Enter name");
            nameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Enter surname");
            surnameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            addressEditText.setError("Enter address");
            addressEditText.requestFocus();
            return;
        }

        // Create customer object
        Customer customer = new Customer("", name, surname, address, paymentMethod);

        // Get reference to Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference currentUserRef = database.getReference("users").child(currentUser.getUid());

        // Save customer object under the current user's node
        currentUserRef.child("customer_details").setValue(customer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddCustomerDetails.this, "Customer details saved successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddCustomerDetails.this, "Failed to save customer details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}