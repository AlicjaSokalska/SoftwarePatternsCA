package com.example.softwarepatternsca;

public class UserReviews {
    private String userEmail;
    private float rating;
    private String comment;

    public UserReviews() {
        // Default constructor required for Firebase
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserReviews(String userEmail, float rating, String comment) {
        this.userEmail = userEmail;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
