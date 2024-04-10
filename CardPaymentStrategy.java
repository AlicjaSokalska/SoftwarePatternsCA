package com.example.softwarepatternsca;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class CardPaymentStrategy implements PaymentStrategy {
    private Context context;
    private double totalAmount;

    public CardPaymentStrategy(Context context, double totalAmount) {
        this.context = context;
        this.totalAmount = totalAmount;
    }

    @Override
    public void processPayment() {
        showCardDetailsDialog();
    }

    private void showCardDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Card Details");

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_card_details, null);
        builder.setView(view);

        EditText cardNumberEditText = view.findViewById(R.id.cardNumberEditText);
        EditText cardNameEditText = view.findViewById(R.id.cardNameEditText);
        EditText cvvEditText = view.findViewById(R.id.cvvEditText);

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cardNumber = cardNumberEditText.getText().toString();
                String cardName = cardNameEditText.getText().toString();
                String cvv = cvvEditText.getText().toString();
                if (!validateCardDetails(cardNumber, cardName, cvv)) {
                    Toast.makeText(context, "Invalid card details. Please check and try again.", Toast.LENGTH_SHORT).show();

                    return;
                }

                navigateToReceiptActivity();
                // Use card details for payment processing
                // Update stock levels in Firebase, display receipt, etc.
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


    private boolean validateCardDetails(String cardNumber, String cardName, String cvv) {

        if (cardNumber.length() < 16 || cardNumber.length() > 19) {

            return false;
        }
        if (cvv.length() != 3 || !cvv.matches("\\d+")) {

            return false;
        }
        if (cardName.isEmpty()) {

            return false;
        }
        return true;
    }

    private void navigateToReceiptActivity() {
        String transactionNumber = generateTransactionNumber();
        Intent intent = new Intent(context, ReceiptActivity.class);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("paymentMethod", "Card");
        intent.putExtra("transactionNumber", transactionNumber);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private String generateTransactionNumber() {
        return "TXN-" + System.currentTimeMillis();
    }
}
