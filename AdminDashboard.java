package com.example.softwarepatternsca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnViewProducts = findViewById(R.id.btnViewProducts);
        Button btnViewCustomers = findViewById(R.id.btnViewCustomers);

        btnViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProductActivity
                Intent intent = new Intent(AdminDashboard.this,ViewProductsAdmin.class);
                startActivity(intent);
            }
        });

        btnViewCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CustomerActivity
                Intent intent = new Intent(AdminDashboard.this, ViewCustomersAdmin.class);
                startActivity(intent);
            }
        });
    }
}
