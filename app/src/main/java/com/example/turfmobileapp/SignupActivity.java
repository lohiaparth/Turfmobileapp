package com.example.turfmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText nameSignup, emailSignup, contactSignup, ageSignup, passwordSignup, repasswordSignup;
    private Button btnSignup;
    private TextView loginTextView; // TextView for "Log in"
    private RadioGroup userTypeGroup;
    private String userType = "User"; // Default user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance

        // Initialize UI components
        initializeUI();

        // Handle the "Log in" text click to navigate back to the LoginActivity
        loginTextView.setOnClickListener(v -> navigateToLogin());

        // Handle sign up process
        btnSignup.setOnClickListener(v -> handleSignup());

        // Handle user type selection
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            userType = selectedRadioButton.getText().toString(); // Get selected user type
        });
    }

    private void initializeUI() {
        nameSignup = findViewById(R.id.nameSignup);
        emailSignup = findViewById(R.id.emailSignup);
        contactSignup = findViewById(R.id.contactSignup);
        ageSignup = findViewById(R.id.ageSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        repasswordSignup = findViewById(R.id.repasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        loginTextView = findViewById(R.id.loginTextView); // Find the "Log in" TextView
        userTypeGroup = findViewById(R.id.userTypeGroup);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the SignupActivity
    }

    private void handleSignup() {
        String name = nameSignup.getText().toString().trim();
        String email = emailSignup.getText().toString().trim();
        String contact = contactSignup.getText().toString().trim();
        String ageString = ageSignup.getText().toString().trim();
        String password = passwordSignup.getText().toString().trim();
        String rePassword = repasswordSignup.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || ageString.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageString);
        if (age < 0) {
            Toast.makeText(SignupActivity.this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user in Firebase
        registerUser(email, password, name, contact, ageString);
    }

    private void registerUser(String email, String password, String name, String contact, String age) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Store additional user details in Firestore
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Create a User object
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("contact", contact);
                        user.put("age", age);
                        user.put("userType", userType); // This stores whether the user is "User", "Owner", or "Admin"

                        // Add a new document with the user ID as the document ID
                        db.collection("users").document(userId).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity(); // Optionally navigate to the main activity
                                })
                                .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(SignupActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the SignupActivity
    }
}