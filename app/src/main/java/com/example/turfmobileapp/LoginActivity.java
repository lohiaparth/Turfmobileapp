package com.example.turfmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button btnLogin;
    private TextView tvSignup;
    private FirebaseAuth auth;  // Declare FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Authenticate the user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sign-in success, navigate to another activity
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        // Get userType from Firestore
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(user.getUid())
                                                .get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        String userType = documentSnapshot.getString("userType");

                                                        // Pass userType to MainActivity
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        intent.putExtra("userType", userType);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "User not found in Firestore", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(LoginActivity.this, "Error retrieving userType: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
}