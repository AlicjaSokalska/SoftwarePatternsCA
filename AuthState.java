package com.example.softwarepatternsca;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

public interface AuthState {
    void authenticate(FirebaseAuth mAuth, String email, String password, Activity activity);
}
