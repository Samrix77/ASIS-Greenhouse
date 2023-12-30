package com.example.asisgreenhouse_v8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    // Reference to the Firebase Realtime Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize FirebaseAuth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        EditText fullName = findViewById(R.id.editTextFullName);
        EditText email = findViewById(R.id.editTextEmail);
        EditText address = findViewById(R.id.editTextAddress);
        EditText phone = findViewById(R.id.editTextPhone);
        EditText password = findViewById(R.id.editTextPassword);
        Button registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fullNameValue = fullName.getText().toString().trim();
                String emailValue = email.getText().toString().trim();
                String addressValue = address.getText().toString().trim();
                String phoneValue = phone.getText().toString();
                String passwordValue = password.getText().toString().trim();

                if (TextUtils.isEmpty(fullNameValue) || TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(addressValue) || TextUtils.isEmpty(phoneValue) || TextUtils.isEmpty(passwordValue)) {
                    Toast.makeText(RegistrationActivity.this, "some data are missing", Toast.LENGTH_SHORT).show();
                } else if (passwordValue.length() < 8) {
                    Toast.makeText(RegistrationActivity.this, "password should be not less than 8 digits", Toast.LENGTH_SHORT).show();
                } else if (phoneValue.length() != 8) {
                    Toast.makeText(RegistrationActivity.this, "phone number not correct", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(emailValue, passwordValue, fullNameValue, addressValue, phoneValue);
                }

            }
        });

        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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
                    startActivity(new Intent(getApplicationContext(), OrderActivity.class));
                    overridePendingTransition(0, 0);
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

    private void registerUser(String email, String password, String fullname, String address, String phone) {
        // First, try to sign in to detect if the email already exists
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // If successful, it means the email is already in use
                        Toast.makeText(RegistrationActivity.this, "Email already in use. Please sign in.", Toast.LENGTH_SHORT).show();
                    } else {
                        // If there was an error, check the type of error
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // Email not in use. Proceed with registration.
                            createUser(email, password, fullname, address, phone);
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // Email does not exist, so proceed to register the user
                            createUser(email, password, fullname, address, phone);
                        } else {
                            // Other errors, possibly network issues or server down
                            Toast.makeText(RegistrationActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser(String email, String password, String fullname, String address, String phone) {
        firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration success

                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create a User object to store additional information
                            UserInfo user = new UserInfo();
                            user.setEmail(email);
                            user.setFullname(fullname);
                            user.setPhone(phone);
                            user.setAddress(address);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserInfo");

                            // Save the User object in the Realtime Database
                            databaseReference.child(userId).setValue(user)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(RegistrationActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        Toast.makeText(RegistrationActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    } else {
                        // Registration failed
                        Log.d("myTag", "This is my message");
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

