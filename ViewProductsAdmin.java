package com.example.softwarepatternsca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
public class ViewProductsAdmin extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Uri selectedImageUri; // Initialize selectedImageUri here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products_admin);

        // Initialize RecyclerView and productList
        recyclerView = findViewById(R.id.recyclerViewProducts);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        // Fetch products from Firebase
        fetchProductsFromFirebase();

        // Set OnClickListener for Add Product button
        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });
    }

    private void fetchProductsFromFirebase() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching products", databaseError.toException());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Log.d("ImageSelection", "Selected Image URI: " + selectedImageUri); // Add logging statement
            // Use Glide to load the image into the ImageView
            ImageView imageViewSelectedImage = findViewById(R.id.imageViewSelectedImage);
            if (imageViewSelectedImage != null) {
                Glide.with(this)
                        .load(selectedImageUri)
                        .apply(new RequestOptions().centerCrop())
                        .into(imageViewSelectedImage);
            } else {
                Log.e("Error", "ImageView is null");
            }
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Product");
        View view = getLayoutInflater().inflate(R.layout.dialog_add_product, null);
        builder.setView(view);

        final EditText editTextProductName = view.findViewById(R.id.editTextProductName);
        final EditText editTextManufacturer = view.findViewById(R.id.editTextManufacturer);
        final EditText editTextPrice = view.findViewById(R.id.editTextPrice);
        final EditText editTextCategory = view.findViewById(R.id.editTextCategory);
        final EditText editTextStockLevel = view.findViewById(R.id.editTextStockLevel);
        final EditText editTextReviews = view.findViewById(R.id.editTextReviews); // New EditText for reviews
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        ImageView imageViewSelectedImage = view.findViewById(R.id.imageViewSelectedImage);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image selection dialog or gallery here
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String productName = editTextProductName.getText().toString();
                final String manufacturer = editTextManufacturer.getText().toString();
                final double price = Double.parseDouble(editTextPrice.getText().toString());
                final String category = editTextCategory.getText().toString();
                final int stockLevel = Integer.parseInt(editTextStockLevel.getText().toString());
                final int reviews = Integer.parseInt(editTextReviews.getText().toString()); // Retrieve reviews

                if (selectedImageUri != null) {
                    // Upload image to Firebase Storage
                    final StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("product_images").child(selectedImageUri.getLastPathSegment());
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get the download URL of the uploaded image
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            // Save product details along with image URL to Realtime Database
                                            DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
                                            String productId = productsRef.push().getKey();
                                            Product product = new Product(productId, productName, manufacturer, price, category, downloadUri.toString(), stockLevel, reviews);
                                            productsRef.child(productId).setValue(product);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle unsuccessful uploads
                                    Toast.makeText(ViewProductsAdmin.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Handle the case where no image is selected
                    Toast.makeText(ViewProductsAdmin.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}