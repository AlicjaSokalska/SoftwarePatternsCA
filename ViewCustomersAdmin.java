package com.example.softwarepatternsca;

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
        customerAdapter = new CustomerAdapter(customers);
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
                    // Assuming "customer_details" is a child node under each user
                    DataSnapshot detailsSnapshot = snapshot.child("customer_details");
                    // Retrieve the fields under "customer_details"
                    String name = detailsSnapshot.child("name").getValue(String.class);
                    String surname = detailsSnapshot.child("surname").getValue(String.class);
                    String address = detailsSnapshot.child("address").getValue(String.class);
                    String paymentMethod = detailsSnapshot.child("paymentMethod").getValue(String.class);
                    // Retrieve email from the user node
                    String email = snapshot.child("email").getValue(String.class);
                    // Create a Customer object
                    Customer customer = new Customer(email, name, surname, address, paymentMethod);
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
