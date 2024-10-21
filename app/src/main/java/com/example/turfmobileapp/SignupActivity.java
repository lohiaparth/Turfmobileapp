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

    private EditText nameSignup, emailSignup, contactSignup, ageSignup, passwordSignup, repasswordSignup;
    private Button btnSignup;
    private TextView loginTextView; // TextView for "Log in"
    private RadioGroup userTypeGroup;
    private RadioButton selectedRadioButton;
    private String userType = "User"; // Default user type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        nameSignup = findViewById(R.id.nameSignup);
        emailSignup = findViewById(R.id.emailSignup);
        contactSignup = findViewById(R.id.contactSignup);
        ageSignup = findViewById(R.id.ageSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        repasswordSignup = findViewById(R.id.repasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        loginTextView = findViewById(R.id.loginTextView); // Find the "Log in" TextView
        userTypeGroup = findViewById(R.id.userTypeGroup);

        // Handle the "Log in" text click to navigate back to the LoginActivity
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the SignupActivity
            }
        });

        // Handle sign up process (previous code logic remains here)
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameSignup.getText().toString();
                String email = emailSignup.getText().toString();
                String contact = contactSignup.getText().toString();
                String age = ageSignup.getText().toString();
                String password = passwordSignup.getText().toString();
                String rePassword = repasswordSignup.getText().toString();

                if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || age.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(rePassword)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Register user in Firebase
                    registerUser(email, password);
                    Toast.makeText(SignupActivity.this, "Signup Successful as " + userType, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle RadioGroup selection (userType logic remains unchanged)
        userTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioButton = findViewById(checkedId);
                if (checkedId == R.id.radioUser) {
                    userType = "User";
                } else if (checkedId == R.id.radioOwner) {
                    userType = "Owner";
                } else if (checkedId == R.id.radioAdmin) {
                    userType = "Admin";
                }
            }
        });
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Store additional user details in Firestore
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userId = firebaseUser.getUid();

                        // Create a User object
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", nameSignup.getText().toString());
                        user.put("email", email);
                        user.put("contact", contactSignup.getText().toString());
                        user.put("age", ageSignup.getText().toString());
                        user.put("userType", userType); // This stores whether the user is "User", "Owner", or "Admin"

                        // Add a new document with a generated ID
                        db.collection("users").document(userId).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(SignupActivity.this, "Failed to save user info", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(SignupActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
