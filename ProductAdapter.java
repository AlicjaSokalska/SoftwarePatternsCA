package com.example.softwarepatternsca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwarepatternsca.Product;
import com.example.softwarepatternsca.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;
    private FirebaseAuth firebaseAuth;

    public ProductAdapter(List<Product> productList) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // Check if the user is an admin before showing options dialog
                isAdmin(new AdminCheckListener() {
                    @Override
                    public void onAdminChecked(boolean isAdmin) {
                        if (isAdmin) {
                            showOptionsDialog(product);
                        } else {
                            Toast.makeText(context, "Only admins can perform this action", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void showOptionsDialog(final Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Options");
        CharSequence[] options = {"Update", "Delete"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Update option selected
                    showUpdateProductDialog(product);
                } else if (which == 1) {
                    // Delete option selected
                    deleteProduct(product);
                }
            }
        });
        builder.create().show();
    }

    private void showUpdateProductDialog(final Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Product");
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        final EditText editTextProductName = view.findViewById(R.id.editTextProductName);
        final EditText editTextManufacturer = view.findViewById(R.id.editTextManufacturer);
        final EditText editTextPrice = view.findViewById(R.id.editTextPrice);
        final EditText editTextCategory = view.findViewById(R.id.editTextCategory);
        final EditText editTextStockLevel = view.findViewById(R.id.editTextStockLevel);
        ImageView imageViewSelectedImage = view.findViewById(R.id.imageViewSelectedImage);


        // Populate dialog with current product details
        editTextProductName.setText(product.getName());
        editTextManufacturer.setText(product.getManufacturer());
        editTextPrice.setText(String.valueOf(product.getPrice()));
        editTextCategory.setText(product.getCategory());
        editTextStockLevel.setText(String.valueOf(product.getStockLevel()));

        // Load current image using Glide if available
        Glide.with(context)
                .load(product.getImageUri())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageViewSelectedImage);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve updated details from EditText fields
                String updatedName = editTextProductName.getText().toString();
                String updatedManufacturer = editTextManufacturer.getText().toString();
                double updatedPrice = Double.parseDouble(editTextPrice.getText().toString());
                String updatedCategory = editTextCategory.getText().toString();
                int updatedStockLevel = Integer.parseInt(editTextStockLevel.getText().toString());
                // Update product in Firebase
                updateProduct(product.getId(), updatedName, updatedManufacturer, updatedPrice, updatedCategory, updatedStockLevel, product.getImageUri());
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }


    private void updateProduct(String productId, String name, String manufacturer, double price, String category, int stockLevel, String imageUri) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        Product updatedProduct = new Product(productId, name, manufacturer, price, category, imageUri, stockLevel, 0); // Pass the existing imageUri
        productRef.setValue(updatedProduct);
    }


    private void deleteProduct(Product product) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(product.getId());
        productRef.removeValue();
    }
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productManufacturerTextView;
        TextView productPriceTextView;
        TextView productStockTextView;
        TextView productCategoryTextView;
        ImageView productImageView;
        TextView productReviewTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productManufacturerTextView = itemView.findViewById(R.id.productManufacturerTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productStockTextView = itemView.findViewById(R.id.productStockTextView); // Added for stock
            productCategoryTextView = itemView.findViewById(R.id.productCategoryTextView); // Added for category
            productImageView = itemView.findViewById(R.id.productImageView);
            productReviewTextView = itemView.findViewById(R.id.productReviewTextView);
        }

        public void bind(Product product) {
            productNameTextView.setText("Name: " + product.getName());
            productManufacturerTextView.setText("Manufacturer: " + product.getManufacturer());
            productPriceTextView.setText("Price: €" + product.getPrice());
            productStockTextView.setText("Stock: " + product.getStockLevel()); // Display stock
            productCategoryTextView.setText("Category: " + product.getCategory()); // Display category
            productReviewTextView.setText("Rating: " + product.getReviews() + "/5"); // Display review

            // Load product image using Glide with the context from the adapter
            Glide.with(context)
                    .load(product.getImageUri())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(productImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityInputDialog(product, context);
                }
            });
        }}
    public interface AdminCheckListener {
        void onAdminChecked(boolean isAdmin);
    }

    private void isAdmin(AdminCheckListener adminCheckListener) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            DatabaseReference adminsRef = FirebaseDatabase.getInstance().getReference("admins");
            adminsRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = dataSnapshot.exists();
                    adminCheckListener.onAdminChecked(isAdmin); // Notify listener with the result
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    adminCheckListener.onAdminChecked(false); // Notify listener with default value
                }
            });
        } else {
            adminCheckListener.onAdminChecked(false); // Notify listener with default value
        }
    }
    private void showQuantityInputDialog(Product product, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Quantity");

        // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();
                int quantity = inputText.isEmpty() ? 0 : Integer.parseInt(inputText);
                if (quantity <= product.getStockLevel()) {
                    // Add product to cart with specified quantity
                    addToCart(product, quantity);
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Quantity exceeds stock level", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addToCart(Product product, int quantity) {
        // Get the current user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Create a reference to the user's cart node
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart");

            // Check if the product is already in the cart
            cartRef.child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // If yes, update the quantity
                        int currentQuantity = dataSnapshot.child("quantity").getValue(Integer.class);
                        cartRef.child(product.getId()).child("quantity").setValue(currentQuantity + quantity);
                    } else {
                        // If the product is not in the cart, add it as a new item
                        cartRef.child(product.getId()).child("name").setValue(product.getName());
                        cartRef.child(product.getId()).child("price").setValue(product.getPrice());
                        cartRef.child(product.getId()).child("quantity").setValue(quantity);
                    }
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                    Toast.makeText(context, "Failed to add product to cart", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User is not authenticated, handle this case accordingly
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

}



