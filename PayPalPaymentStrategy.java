package com.example.softwarepatternsca;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class PayPalPaymentStrategy implements PaymentStrategy {
    private Context context;
    private double totalAmount;

    public PayPalPaymentStrategy(Context context, double totalAmount) {
        this.context = context;
        this.totalAmount = totalAmount;
    }

    @Override
    public void processPayment() {
        // Implement logic for PayPal payment processing
        // Show PayPal login dialog
        showPayPalDialog();
    }

    private void showPayPalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("PayPal Login");

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_paypal, null);
        builder.setView(view);

        EditText usernameEditText = view.findViewById(R.id.usernameEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);

        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                navigateToReceiptActivity();
                // You can perform further actions with the username and password, like authentication
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void navigateToReceiptActivity() {
        // Generate a transaction number
        String transactionNumber = generateTransactionNumber();
        // Start the receipt activity
        Intent intent = new Intent(context, ReceiptActivity.class);
        intent.putExtra("totalAmount", ((CheckoutActivity) context).getTotalAmount());
        intent.putExtra("paymentMethod", "PayPal");
        intent.putExtra("transactionNumber", transactionNumber);
        context.startActivity(intent);
        ((Activity) context).finish(); // Optional: Finish the current activity if you don't need to go back to it
    }

    private String generateTransactionNumber() {
        // Generate a unique transaction number here
        return "TXN-" + System.currentTimeMillis();
    }
}
