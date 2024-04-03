package com.example.softwarepatternsca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;
    private TransactionBindingStrategy bindingStrategy;
    private TransactionVisitor visitor; // Added member for visitor

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setBindingStrategy(TransactionBindingStrategy bindingStrategy) {
        this.bindingStrategy = bindingStrategy;
    }

    // Method to set the visitor
    public void setVisitor(TransactionVisitor visitor) {
        this.visitor = visitor;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        if (transactions != null && position < transactions.size()) {
            Transaction transaction = transactions.get(position);
            if (bindingStrategy != null) {
                bindingStrategy.bind(holder, transaction);
            } else {
                holder.bind(transaction);
            }
            // If a visitor is set, visit the transaction
            if (visitor != null) {
                visitor.visit(transaction);
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView paymentMethodTextView;
        TextView transactionNumberTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.text_view_amount);
            paymentMethodTextView = itemView.findViewById(R.id.text_view_payment_method);
            transactionNumberTextView = itemView.findViewById(R.id.text_view_transaction_number);
        }

        public void bind(Transaction transaction) {
            amountTextView.setText(String.valueOf(transaction.getTotalAmount()));
            paymentMethodTextView.setText(transaction.getPaymentMethod());
            transactionNumberTextView.setText(transaction.getTransactionNumber());
        }
    }

    public interface TransactionBindingStrategy {
        void bind(TransactionViewHolder holder, Transaction transaction);
    }
}
