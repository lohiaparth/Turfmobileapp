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

    private EditText teamName;
    private TextView dateText, timeText;
    private Button datePickerBtn, timePickerBtn, payNowBtn;
    private TextView timeSlotText;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        db = FirebaseFirestore.getInstance();

        teamName = findViewById(R.id.firstName);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        datePickerBtn = findViewById(R.id.datePickerBtn);
        timePickerBtn = findViewById(R.id.timePickerBtn);
        payNowBtn = findViewById(R.id.payNowBtn);
        timeSlotText = findViewById(R.id.timeSlotText);


        datePickerBtn.setOnClickListener(v -> showDatePickerDialog());
        timePickerBtn.setOnClickListener(v -> showTimePickerDialog());
        payNowBtn.setOnClickListener(v -> goToPayments());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> dateText.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    // Check if the time falls between 4:00 AM and 12:00 AM
                    if (hourOfDay >= 4 || hourOfDay == 0) {
                        String startTime = String.format("%02d:%02d", hourOfDay, minute);
                        String endTime = String.format("%02d:%02d", (hourOfDay + 1) % 24, minute);
                        timeText.setText(startTime + " - " + endTime);
                        timeSlotText.setText("Selected Time Slot: " + startTime + " - " + endTime);
                    } else {
                        Toast.makeText(this, "Time must be between 4:00 AM and 12:00 AM", Toast.LENGTH_SHORT).show();
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY), 0, true);
        timePickerDialog.show();
    }

    private void goToPayments() {
        if (dateText.getText().toString().isEmpty() || timeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show();
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

        // Save booking to Firestore
        db.collection("bookings").add(new Booking(team, date, time))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking saved successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Booking saved with ID: " + documentReference.getId());

                    // Proceed to payment activity
                    Intent intent = new Intent(BookingPage.this, Payments.class);
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
        dateText.setText("");
        timeText.setText("");
    }

    // Define the Booking class to represent booking data
    public class Booking {
        private String teamName;
        private String dateText;
        private String timeText;

        public Booking(String teamName, String dateText, String timeText) {
            this.teamName = teamName;
            this.dateText = dateText;
            this.timeText = timeText;
        }

        // Getters are required for Firestore serialization
        public String getTeamName() { return teamName; }
        public String getDateText() { return dateText; }
        public String getTimeText() { return timeText; }
    }
}
