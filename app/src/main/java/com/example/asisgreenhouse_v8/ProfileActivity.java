package com.example.asisgreenhouse_v8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


    public class ProfileActivity extends AppCompatActivity {
        private TextView ui_name,ui_email,ui_phone,ui_address,tvUserInfo;
        private FirebaseAuth auth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);


            ui_name = findViewById(R.id.ui_name);
            ui_email = findViewById(R.id.ui_email);
            ui_phone = findViewById(R.id.ui_phone);
            ui_address = findViewById(R.id.ui_address);

            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                DatabaseReference userInfoRef = databaseReference.child("UserInfo");

                // Query the database to find the user profile that matches the email
                userInfoRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through the snapshots to get user data (assuming unique emails)
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                UserInfo userInfo = userSnapshot.getValue(UserInfo.class);
                                if (userInfo != null) {
                                    // Format the user's information as an XML string

                                    ui_name.setText(userInfo.getFullname());
                                    ui_email.setText(userInfo.getEmail());
                                    ui_phone.setText(userInfo.getPhone());
                                    ui_address.setText(userInfo.getAddress());


                                } else {
                                    ui_name.setText("");
                                    ui_email.setText("");
                                    ui_phone.setText("");
                                    ui_address.setText("");
                                    tvUserInfo.setText("User data is null!");
                                }
                            }
                        } else {
                            ui_name.setText("");
                            ui_email.setText("");
                            ui_phone.setText("");
                            ui_address.setText("");
                            tvUserInfo.setText("User not found.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        tvUserInfo.setText("Database error: " + databaseError.getMessage());
                    }
                });
            } else {
                tvUserInfo.setText("No current user logged in.");
            }









            // Initialize and assign variable
            BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
            // Perform item selected listener
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.notification){
                        startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                        overridePendingTransition(0,0);
                        return true;}
                    else if (itemId == R.id.home){
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;}
                    else if (itemId == R.id.store){
                        startActivity(new Intent(getApplicationContext(),StoreActivity.class));
                        overridePendingTransition(0,0);
                        return true;}
                    else if (itemId == R.id.order){
                        startActivity(new Intent(getApplicationContext(),OrderActivity.class));
                        overridePendingTransition(0,0);
                        return true;}
                    return false;
                };

            });






        }
    }