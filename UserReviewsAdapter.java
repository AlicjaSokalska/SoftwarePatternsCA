package com.example.softwarepatternsca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;

import java.util.List;
public class UserReviewsAdapter extends RecyclerView.Adapter<UserReviewsAdapter.UserReviewViewHolder> {
    private List<UserReviews> userReviewsList;

    public UserReviewsAdapter(List<UserReviews> userReviewsList) {
        this.userReviewsList = userReviewsList;
    }

    @NonNull
    @Override
    public UserReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for user review item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_review, parent, false);
        return new UserReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserReviewViewHolder holder, int position) {
        // Bind data to user review item
        UserReviews userReview = userReviewsList.get(position);
        holder.bind(userReview);
    }

    @Override
    public int getItemCount() {
        return userReviewsList.size();
    }

    // ViewHolder class for user review item
    public static class UserReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userEmailTextView;
        TextView ratingTextView;
        TextView commentTextView;

        public UserReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTextView = itemView.findViewById(R.id.userEmailTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }

        public void bind(UserReviews userReview) {
            // Bind data to user review item
            String userEmail = userReview.getUserEmail();
            float rating = userReview.getRating();
            String comment = userReview.getComment();

            userEmailTextView.setText("User: " + userEmail);
            ratingTextView.setText("Rating: " + rating);
            commentTextView.setText("Comment: " + comment);
        }
    }
}