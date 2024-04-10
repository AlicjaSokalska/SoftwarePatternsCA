package com.example.softwarepatternsca;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

// ReviewActivity.java
public class ReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserReviewsAdapter adapter;
    private List<UserReviews> userReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userReviewsList = new ArrayList<>();
        adapter = new UserReviewsAdapter(userReviewsList);
        recyclerView.setAdapter(adapter);

        // Retrieve product ID from intent extras
        String productId = getIntent().getStringExtra("productId");

        // Fetch user reviews for the selected product from Firebase
        fetchUserReviewsFromFirebase(productId);
    }

    private void fetchUserReviewsFromFirebase(String productId) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("products").child(productId).child("userReviews");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = reviewSnapshot.child("userEmail").getValue(String.class);
                    float rating = reviewSnapshot.child("rating").getValue(Float.class);
                    String comment = reviewSnapshot.child("comment").getValue(String.class);
                    UserReviews userReview = new UserReviews(userEmail, rating, comment);
                    userReviewsList.add(userReview);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity", "Failed to fetch user reviews: " + databaseError.getMessage());
                Toast.makeText(ReviewActivity.this, "Failed to fetch user reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Finish the activity when back button is pressed
    }
}
