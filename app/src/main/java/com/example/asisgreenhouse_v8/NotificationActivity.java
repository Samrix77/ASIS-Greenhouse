package com.example.asisgreenhouse_v8;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotificationActivity extends AppCompatActivity {





    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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
        setContentView(R.layout.activity_notification);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ImageView loginIcon = findViewById(R.id.loginIcon_btn);
        ImageView signoutIcon = findViewById(R.id.signoutIcon_btn);
        ImageView profileIcon = findViewById(R.id.profileIcon_btn);





        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.notification);
        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.store){
                    startActivity(new Intent(getApplicationContext(),StoreActivity.class));
                    overridePendingTransition(0,0);
                    return true;}
                else if (itemId == R.id.notification){
                    return true;}
                else if (itemId == R.id.home){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;}
                else if (itemId == R.id.order){
                    startActivity(new Intent(getApplicationContext(),OrderActivity.class));
                    overridePendingTransition(0,0);
                    return true;}
                return false;
            };

        });



        loginIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        signoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView logoutIcon = findViewById(R.id.signoutIcon_btn);
                ImageView profileIcon = findViewById(R.id.profileIcon_btn);
                ImageView loginIcon = findViewById(R.id.loginIcon_btn);
                FirebaseAuth.getInstance().signOut();
                logoutIcon.setVisibility(View.GONE);
                profileIcon.setVisibility(View.GONE);
                loginIcon.setVisibility(View.VISIBLE);
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
            }
        });


    }
}
