package com.example.softwarepatternsca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactions;
    private DatabaseReference databaseReference;
    private String customerEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("email")) {
            customerEmail = intent.getStringExtra("email");
            if (customerEmail != null) {
                recyclerView = findViewById(R.id.recycler_view_transactions);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                transactions = new ArrayList<>();
                transactionAdapter = new TransactionAdapter(transactions);
                recyclerView.setAdapter(transactionAdapter);

                databaseReference = FirebaseDatabase.getInstance().getReference("users");
                fetchTransactions();
            } else {
                Log.e("HistoryActivity", "Email is null");
            }
        } else {
            Log.e("HistoryActivity", "Intent is null or doesn't contain email extra");
        }
    }


    private void fetchTransactions() {
        databaseReference.orderByChild("email").equalTo(customerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerUid = dataSnapshot.getChildren().iterator().next().getKey();
                    DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("users").child(customerUid).child("transactions");
                    transactionsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            transactions.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Transaction transaction = snapshot.getValue(Transaction.class);
                                transactions.add(transaction);
                                transaction.accept(new AdminTransactionVisitor());
                            }
                            transactionAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("HistoryActivity", "Database Error: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e("HistoryActivity", "User not found for email: " + customerEmail);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HistoryActivity", "Database Error: " + databaseError.getMessage());

            }
        });
    }
}

