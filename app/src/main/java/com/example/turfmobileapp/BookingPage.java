package com.example.turfmobileapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class BookingPage extends AppCompatActivity {

    private static final String TAG = "BookingPage";

    private EditText teamName, hours;
    private TextView dateText, timeText, finalAmount;
    private Button datePickerBtn, timePickerBtn, calculateAmountBtn, payNowBtn;

    private FirebaseFirestore db;
    private double turfPrice;
    private int selectedHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        db = FirebaseFirestore.getInstance();
        turfPrice = getIntent().getDoubleExtra("turfPrice", 0.0);

        teamName = findViewById(R.id.firstName);
        hours = findViewById(R.id.hours);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        finalAmount = findViewById(R.id.finalAmount);
        datePickerBtn = findViewById(R.id.datePickerBtn);
        timePickerBtn = findViewById(R.id.timePickerBtn);
        calculateAmountBtn = findViewById(R.id.calculateAmountBtn);
        payNowBtn = findViewById(R.id.payNowBtn);

        datePickerBtn.setOnClickListener(v -> showDatePickerDialog());
        timePickerBtn.setOnClickListener(v -> showTimePickerDialog());
        calculateAmountBtn.setOnClickListener(v -> calculateFinalAmount());
        payNowBtn.setOnClickListener(v -> goToPayments());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> dateText.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> timeText.setText(hourOfDay + ":" + String.format("%02d", minute)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void calculateFinalAmount() {
        if (hours.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter the number of hours", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dateText.getText().toString().isEmpty() || timeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            selectedHours = Integer.parseInt(hours.getText().toString().trim());
            if (selectedHours <= 0) {
                Toast.makeText(this, "Please enter a valid number of hours", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount = turfPrice * selectedHours;
            finalAmount.setText(String.format("Total: Rs. %.2f", amount));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number of hours", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid hours input: " + e.getMessage());
        }
    }

    private void goToPayments() {
        if (finalAmount.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please calculate the final amount before proceeding", Toast.LENGTH_SHORT).show();
            return;
        }

        if (teamName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your team name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve data from input fields
        String team = teamName.getText().toString().trim();
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();
        String amount = finalAmount.getText().toString().replace("Total: Rs. ", ""); // Extract amount value
        int hoursBooked = selectedHours;

        // Save booking to Firestore
        db.collection("bookings").add(new Booking(team, date, time, amount, hoursBooked))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Booking saved with ID: " + documentReference.getId());

                    // Proceed to payment activity
                    Intent intent = new Intent(BookingPage.this, Payments.class);
                    intent.putExtra("finalAmount", finalAmount.getText().toString());
                    intent.putExtra("date", dateText.getText().toString());
                    intent.putExtra("time", timeText.getText().toString());
                    startActivity(intent);

                    // Clear fields after saving
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving booking. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error saving booking", e);
                });
    }

    private void clearFields() {
        teamName.setText("");
        hours.setText("");
        dateText.setText("");
        timeText.setText("");
        finalAmount.setText("");
    }

    // Define the Booking class to represent booking data
    public class Booking {
        private String teamName;
        private String dateText;
        private String timeText;
        private String finalAmount;
        private int hours;

        public Booking(String teamName, String dateText, String timeText, String finalAmount, int hours) {
            this.teamName = teamName;
            this.dateText = dateText;
            this.timeText = timeText;
            this.finalAmount = finalAmount;
            this.hours = hours;
        }

        // Getters are required for Firestore serialization
        public String getTeamName() { return teamName; }
        public String getDateText() { return dateText; }
        public String getTimeText() { return timeText; }
        public String getFinalAmount() { return finalAmount; }
        public int getHours() { return hours; }
    }
}
