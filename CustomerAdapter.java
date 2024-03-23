package com.example.softwarepatternsca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


// RecyclerView Adapter
public  class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<Customer> customers;

    public CustomerAdapter(List<Customer> customers) {
        this.customers = customers;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView nameTextView;
        TextView surnameTextView;
        TextView addressTextView;
        TextView paymentMethodTextView;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.email_textview);
            nameTextView = itemView.findViewById(R.id.name_textview);
            surnameTextView = itemView.findViewById(R.id.surname_textview);
            addressTextView = itemView.findViewById(R.id.address_textview);
            paymentMethodTextView = itemView.findViewById(R.id.payment_method_textview);
        }

        public void bind(Customer customer) {
            emailTextView.setText(customer.getEmail());
            nameTextView.setText("Name: " + customer.getName());
            surnameTextView.setText("Surname: " + customer.getSurname());
            addressTextView.setText("Address: " + customer.getAddress());
            paymentMethodTextView.setText("Payment Method: " + customer.getPaymentMethod());
        }
    }
}
