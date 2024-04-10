package com.example.softwarepatternsca;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class BankTransferPaymentStrategy implements PaymentStrategy {
    private Context context;
    private double totalAmount;

    public BankTransferPaymentStrategy(Context context, double totalAmount) {
        this.context = context;
        this.totalAmount = totalAmount;
    }

    @Override
    public void processPayment() {
        showBankTransferDialog();
    }

    private void showBankTransferDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Bank Transfer Details");

        // Inflate the layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bank_transfer, null);
        builder.setView(view);

        EditText accountNumberEditText = view.findViewById(R.id.accountNumberEditText);
        EditText routingNumberEditText = view.findViewById(R.id.routingNumberEditText);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String accountNumber = accountNumberEditText.getText().toString().trim();
                String routingNumber = routingNumberEditText.getText().toString().trim();
                if (!validateBankTransferDetails(accountNumber, routingNumber)) {
                    return;
                }

                navigateToReceiptActivity();
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

        // Update stock levels in CheckoutActivity
        ((CheckoutActivity) context).updateStock();

        // Start the receipt activity
        Intent intent = new Intent(context, ReceiptActivity.class);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("paymentMethod", "Bank Transfer");
        intent.putExtra("transactionNumber", transactionNumber);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private boolean validateBankTransferDetails(String accountNumber, String routingNumber) {
        if (accountNumber.length() != 12|| routingNumber.length() != 9) {
            return false;
        }   if (!accountNumber.matches("\\d+") || !routingNumber.matches("\\d+")) {
            return false;
        }

        return true;
    }



    private String generateTransactionNumber() {
        return "TXN-" + System.currentTimeMillis();
    }
}
