package com.example.softwarepatternsca;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewCustomersAdmin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customers;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customers_admin);

        customers = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter(customers, new CustomerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String email) {
                Intent intent = new Intent(ViewCustomersAdmin.this, HistoryActivity.class);
                intent.putExtra("email", email); // Pass the email as intent extra
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(customerAdapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve customer data from Firebase Realtime Database
        fetchCustomers();
    }

    private void fetchCustomers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve email from "users" node
                    String email = snapshot.child("email").getValue(String.class);
                    // Navigate to "customer_details" node to retrieve other details
                    DataSnapshot detailsSnapshot = snapshot.child("customer_details");
                    String name = detailsSnapshot.child("name").getValue(String.class);
                    String surname = detailsSnapshot.child("surname").getValue(String.class);
                    String address = detailsSnapshot.child("address").getValue(String.class);
                    String paymentMethod = detailsSnapshot.child("paymentMethod").getValue(String.class);
                    // Create Customer object
                    Customer customer = new Customer.Builder()
                            .email(email)
                            .name(name)
                            .surname(surname)
                            .address(address)
                            .paymentMethod(paymentMethod)
                            .build();
                    customers.add(customer);
                }
                customerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
}