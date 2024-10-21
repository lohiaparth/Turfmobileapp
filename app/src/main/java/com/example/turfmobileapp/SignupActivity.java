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

public class SignupActivity extends AppCompatActivity {

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
                    // Add registration logic here
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
}
