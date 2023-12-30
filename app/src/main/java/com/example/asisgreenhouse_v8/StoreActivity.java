package com.example.asisgreenhouse_v8;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    private EditText etProductName, etProductPrice, etProductPhoto;
    private Button btnAddProduct;
    private ImageView showAddProductForm_btn;
    private ListView lvProducts;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;
    private DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        updateIconBasedOnUser(firebaseAuth.getCurrentUser());
    }

    private void updateIconBasedOnUser(FirebaseUser user) {
        ImageView logoutIcon = findViewById(R.id.signoutIcon_btn);
        ImageView loginIcon = findViewById(R.id.loginIcon_btn);
        ImageView profileIcon = findViewById(R.id.profileIcon_btn);

        if (user != null) {
            // User is signed in, show the logout icon
            loginIcon.setVisibility(View.GONE);
            profileIcon.setVisibility(View.VISIBLE);
            logoutIcon.setVisibility(View.VISIBLE);
        } else {
            // User is signed out, hide the logout icon
            loginIcon.setVisibility(View.VISIBLE);
            profileIcon.setVisibility(View.GONE);
            logoutIcon.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView loginIcon = findViewById(R.id.loginIcon_btn);
        ImageView signoutIcon = findViewById(R.id.signoutIcon_btn);
        ImageView profileIcon = findViewById(R.id.profileIcon_btn);


        // Initialize Firebase components
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Products");

        // Initialize UI components
        lvProducts = findViewById(R.id.lvProducts);

        showAddProductForm_btn = findViewById(R.id.showAddProductForm_btn);   //button to show dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_product);
        btnAddProduct = dialog.findViewById(R.id.btnAddProduct);   //dialog button
        etProductName = dialog.findViewById(R.id.etProductName);   //dialog EditText
        etProductPrice = dialog.findViewById(R.id.etProductPrice); //dialog EditText
        etProductPhoto = dialog.findViewById(R.id.etProductPhoto); //dialog EditText

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View list_item_product_layout = inflater.inflate(R.layout.list_item_product, null);

        productList = new ArrayList<>();

        isAdmin adminCheck = new isAdmin();
        adminCheck.checkIfUserIsAdmin(FirebaseAuth.getInstance(), isAdmin -> {
            runOnUiThread(() -> {
                if (isAdmin) {
                    // User is an admin, update UI
                    showAddProductForm_btn.setVisibility(View.VISIBLE);
                    adapter = new ProductAdapter(this, productList, databaseReference, true);
                    lvProducts.setAdapter(adapter);
                } else {
                    // User is not an admin, update UI
                    showAddProductForm_btn.setVisibility(View.GONE);
                    adapter = new ProductAdapter(this, productList, databaseReference, false);
                    lvProducts.setAdapter(adapter);
                }
            });
        });

        // Add product button click listener
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        // Load products from Firebase
        loadProducts();

        loginIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        signoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                findViewById(R.id.signoutIcon_btn).setVisibility(View.GONE);
                findViewById(R.id.profileIcon_btn).setVisibility(View.GONE);
                findViewById(R.id.loginIcon_btn).setVisibility(View.VISIBLE);
                findViewById(R.id.showAddProductForm_btn).setVisibility(View.GONE);
                adapter = new ProductAdapter(StoreActivity.this, productList, databaseReference, false);
                lvProducts.setAdapter(adapter);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        showAddProductForm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.store);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.notification) {
                    startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.store) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.order) {
                    startActivity(new Intent(getApplicationContext(), OrderActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            };
        });
    }

    private void addProduct() {
        String name = etProductName.getText().toString().trim();
        String priceString = etProductPrice.getText().toString().trim();
        String photo = etProductPhoto.getText().toString().trim();
        if (!name.isEmpty() && !priceString.isEmpty()) {
            double price = Double.parseDouble(priceString);

            // Create a new product
            String id = databaseReference.push().getKey(); // Generate a unique ID for the product
            Product product = new Product(); // Assuming an empty string for photo URL
            product.setName(name);
            product.setPrice(price);
            product.setPhoto(photo);
            product.setId(id);
            databaseReference.child(id).setValue(product);

            // Clear the input fields
            etProductName.setText("");
            etProductPrice.setText("");
            etProductPhoto.setText("");
        }
    }

    private void loadProducts() {
        // Attach a listener to read the data at your products reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the previous product list
                productList.clear();

                // Iterate through all the children in your database reference
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the Product object from the snapshot
                    Product product = postSnapshot.getValue(Product.class);
                    productList.add(product);
                }

                // Notify the adapter to update the UI
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                System.out.println("loadPost:onCancelled " + databaseError.toException());
                // Handle failed read from database
            }
        });
    }


}
