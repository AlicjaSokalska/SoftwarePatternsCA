package com.example.softwarepatternsca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductObserver {

    private RecyclerView recyclerView;
    private Button btnViewCart;


    private SearchView searchView;
    private Spinner spinnerSort;

    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> originalProductList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnViewCart = findViewById(R.id.btn_view_cart);

        searchView = findViewById(R.id.search_view);


        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        ProductManager.getInstance().registerObserver(this);
        // Fetch products from Firebase
        fetchProducts();

        setupSearchView();

        btnViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
        spinnerSort = findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle sorting based on selected option
                String selectedOption = parent.getItemAtPosition(position).toString();
                switch (selectedOption) {
                    case "Price (Low to High)":
                        sortByPriceAscending();
                        break;
                    case "Price (High to Low)":
                        sortByPriceDescending();
                        break;
                    case "Name (A to Z)":
                        sortByNameAscending();
                        break;
                    case "Name (Z to A)":
                        sortByNameDescending();
                        break;
                    case "Manufacturer (A to Z)":
                        sortByManufacturerAscending();
                        break;
                    case "Manufacturer (Z to A)":
                        sortByManufacturerDescending();
                        break;
                    default:
                        // Handle default case
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


    }



    private void sortByPriceAscending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    private void sortByPriceDescending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return Double.compare(o2.getPrice(), o1.getPrice());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    private void sortByNameAscending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    private void sortByNameDescending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    private void sortByManufacturerAscending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getManufacturer().compareToIgnoreCase(o2.getManufacturer());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    private void sortByManufacturerDescending() {
        Collections.sort(productList, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o2.getManufacturer().compareToIgnoreCase(o1.getManufacturer());
            }
        });
        productAdapter.notifyDataSetChanged();
    }

    /*private void fetchProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                // Handle errors
            }
        });
    }*/
    private void fetchProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                if (originalProductList == null) {
                    originalProductList = new ArrayList<>(); // Initialize originalProductList if null
                } else {
                    originalProductList.clear(); // Clear the original list before adding items
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                    originalProductList.add(product); // Add products to the original list
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do nothing on submit, as we're filtering dynamically
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the list based on the search query
                filterProductList(newText);
                return true;
            }
        });

        // Clear search when 'x' is clicked
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Reset the list to its original state
                resetProductList();
                return false;
            }
        });
    }

    private void filterProductList(String query) {
        List<Product> filteredList = new ArrayList<>();
        String searchQuery = query.toLowerCase().trim();

        for (Product product : originalProductList) {
            // Check if the product name, manufacturer, or category contains the search query
            if (product.getName().toLowerCase().contains(searchQuery) ||
                    product.getManufacturer().toLowerCase().contains(searchQuery) ||
                    product.getCategory().toLowerCase().contains(searchQuery)) {
                filteredList.add(product);
            }
        }

        // Update the displayed list with the filtered results
        productList.clear();
        productList.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    protected void onDestroy() {
        super.onDestroy();
        // Unregister MainActivity as an observer from the ProductManager
        ProductManager.getInstance().removeObserver(this);
    }



    @Override
    public void notifyLowStock(Product product) {

    }



    private void resetProductList() {
        // Reset the list to its original state
        productList.clear();
        productList.addAll(originalProductList);
        productAdapter.notifyDataSetChanged();
    }
}




