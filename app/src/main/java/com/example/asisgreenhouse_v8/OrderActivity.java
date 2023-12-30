package com.example.asisgreenhouse_v8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private ListView lvOrders;
    private ArrayList<Order> orderList;
    private OrderAdapter adapter;
    private DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        updateIconBasedOnUser(currentUser);
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
        setContentView(R.layout.activity_order);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView loginIcon = findViewById(R.id.loginIcon_btn);
        ImageView signoutIcon = findViewById(R.id.signoutIcon_btn);
        ImageView profileIcon = findViewById(R.id.profileIcon_btn);

        //--------------------------------------------

        // Initialize Firebase components
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Orders");

        // Initialize UI components
        lvOrders = findViewById(R.id.lvOrders);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View list_item_product_layout = inflater.inflate(R.layout.list_item_product, null);

        orderList = new ArrayList<>();

        isAdmin adminCheck = new isAdmin();
        adminCheck.checkIfUserIsAdmin(FirebaseAuth.getInstance(), isAdmin -> {
            runOnUiThread(() -> {
                if (isAdmin) {
                    // User is an admin, update UI
                    adapter = new OrderAdapter(this, orderList, databaseReference, true);
                    lvOrders.setAdapter(adapter);
                } else {
                    // User is not an admin, update UI
                    adapter = new OrderAdapter(this, orderList, databaseReference, false);
                    lvOrders.setAdapter(adapter);
                }
            });
        });

        // Load orders from Firebase
        loadOrders();

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
                adapter = new OrderAdapter(com.example.asisgreenhouse_v8.OrderActivity.this, orderList, databaseReference, false);
                lvOrders.setAdapter(adapter);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.order);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.store) {
                    startActivity(new Intent(getApplicationContext(), StoreActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.order) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.notification) {
                    startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            };
        });
    }

    private void loadOrders() {
        // Attach a listener to read the data at your products reference
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear the previous product list
                orderList.clear();

                // Iterate through all the children in your database reference
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    // Get the Product object from the snapshot
                    Order order = postSnapshot.getValue(Order.class);

                    orderList.add(order);
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