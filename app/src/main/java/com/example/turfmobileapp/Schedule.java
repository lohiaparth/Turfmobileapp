package com.example.turfmobileapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Schedule extends AppCompatActivity {

    private TextView selectedDateText;
    private Button selectDateButton;
    private GridLayout timetableGridLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        selectedDateText = findViewById(R.id.selectedDateText);
        selectDateButton = findViewById(R.id.selectDateButton);
        timetableGridLayout = findViewById(R.id.timetableGridLayout);
        db = FirebaseFirestore.getInstance();

        selectDateButton.setOnClickListener(view -> showDatePickerDialog());
        loadTimetableForDate(Calendar.getInstance());
    }
    String turfId = getIntent().getStringExtra("turfId");

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateText.setText("Date: " + dateString);

                    loadTimetableForDate(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void loadTimetableForDate(Calendar date) {
        String selectedDate = date.get(Calendar.DAY_OF_MONTH) + "/" +
                (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.YEAR);

        // Reset timetable slots to "Available" before updating with booked slots
        for (int i = 1; i < timetableGridLayout.getChildCount(); i += 2) {
            TextView timeSlot = (TextView) timetableGridLayout.getChildAt(i);
            timeSlot.setText("Available");
        }

        // Fetch bookings for the selected date
        db.collection("bookings")
                .whereEqualTo("dateText", selectedDate) // Ensure "dateText" matches the format used in BookingPage
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No bookings for the selected date", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String timeSlot = document.getString("timeText");
                            String teamName = document.getString("teamName");
                            updateTimetableSlot(timeSlot, teamName);
                        }
                        Toast.makeText(this, "Timetable updated for selected date", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("Schedule", "Error fetching bookings", e));
    }

    private void updateTimetableSlot(String timeSlot, String teamName) {
        // Map each time slot to the corresponding TextView ID
        switch (timeSlot) {
            case "8:00":
                ((TextView) findViewById(R.id.slot_8_9)).setText(teamName);
                break;
            case "9:00":
                ((TextView) findViewById(R.id.slot_9_10)).setText(teamName);
                break;
            case "10:00":
                ((TextView) findViewById(R.id.slot_10_11)).setText(teamName);
                break;
            case "11:00":
                ((TextView) findViewById(R.id.slot_11_12)).setText(teamName);
                break;
            case "12:00":
                ((TextView) findViewById(R.id.slot_12_1)).setText(teamName);
                break;
            case "1:00":
                ((TextView) findViewById(R.id.slot_1_2)).setText(teamName);
                break;
            case "2:00":
                ((TextView) findViewById(R.id.slot_2_3)).setText(teamName);
                break;
            case "3:00":
                ((TextView) findViewById(R.id.slot_3_4)).setText(teamName);
                break;
            case "4:00":
                ((TextView) findViewById(R.id.slot_4_5)).setText(teamName);
                break;
            case "5:00":
                ((TextView) findViewById(R.id.slot_5_6)).setText(teamName);
                break;
            default:
                Log.w("Schedule", "Unexpected time slot: " + timeSlot);
                break;
        }
    }
}
//
//package com.example.turfmobileapp;
//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.GridLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Schedule extends AppCompatActivity {
//
//    private TextView selectedDateText;
//    private Button selectDateButton;
//    private GridLayout timetableGridLayout;
//    private FirebaseFirestore db;
//    private String turfId;
//    private Spinner turfSpinner;
//    private List<String> turfList;
//    private String ownerEmail; // Declare ownerEmail to store the email
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.schedule);
//
//        // Initialize views
//        selectedDateText = findViewById(R.id.selectedDateText);
//        selectDateButton = findViewById(R.id.selectDateButton);
//        timetableGridLayout = findViewById(R.id.timetableGridLayout);
//        turfSpinner = findViewById(R.id.turfSpinner);
//        db = FirebaseFirestore.getInstance();
//
//        // Get turfId and ownerEmail from Intent extras
//        turfId = getIntent().getStringExtra("turfId");
//        ownerEmail = getIntent().getStringExtra("email"); // Retrieve owner email
//
//        selectDateButton.setOnClickListener(view -> showDatePickerDialog());
//        loadTimetableForDate(Calendar.getInstance());
//
//        // Call fetchTurfList to populate the Spinner based on ownerEmail
//        fetchTurfList();
//    }
//
//    private void fetchTurfList() {
//        db.collection("listturfs").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                turfList = new ArrayList<>();
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String turfEmail = document.getString("email"); // Assuming each turf document has an "email" field
//                    String turfName = document.getString("companyName");   // Assuming there's a "name" field
//
//                    // Check if the turf email matches the owner's email
//                    if (turfEmail != null && turfEmail.equals(ownerEmail)) {
//                        turfList.add(turfName); // Add turf name to list if email matches
//                    }
//                }
//
//                // Set up the Spinner adapter
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turfList);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                turfSpinner.setAdapter(adapter);
//
//                if (turfList.isEmpty()) {
//                    Toast.makeText(Schedule.this, "No turfs available for this owner", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Log.e("Schedule", "Error fetching turfs: ", task.getException());
//                Toast.makeText(Schedule.this, "Failed to load turfs", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showDatePickerDialog() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                (view, selectedYear, selectedMonth, selectedDay) -> {
//                    Calendar selectedDate = Calendar.getInstance();
//                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
//
//                    String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
//                    selectedDateText.setText("Date: " + dateString);
//
//                    loadTimetableForDate(selectedDate);
//                }, year, month, day);
//
//        datePickerDialog.show();
//    }
//
//    private void loadTimetableForDate(Calendar date) {
//        String selectedDate = date.get(Calendar.DAY_OF_MONTH) + "/" +
//                (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.YEAR);
//
//        // Clear previous slots from the grid layout
//        timetableGridLayout.removeAllViews();
//
//        // Fetch the bookings for the selected turf and date
//        db.collection("bookings")
//                .whereEqualTo("turfId", turfId)
//                .whereEqualTo("date", selectedDate)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document : task.getResult()) {
//                            // Assuming your booking document has a 'timeSlot' field
//                            String timeSlot = document.getString("timeSlot");
//                            displayTimeSlot(timeSlot);
//                        }
//                    } else {
//                        Log.e("Schedule", "Error getting documents: ", task.getException());
//                        Toast.makeText(Schedule.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void displayTimeSlot(String timeSlot) {
//        TextView timeSlotTextView = new TextView(this);
//        timeSlotTextView.setText(timeSlot);
//        timeSlotTextView.setOnClickListener(v -> bookTimeSlot(timeSlot));
//        timetableGridLayout.addView(timeSlotTextView);
//    }
//
//    private void bookTimeSlot(String timeSlot) {
//        // Logic for booking the selected time slot
//        String bookingId = db.collection("bookings").document().getId(); // Create a new booking ID
//
//        // Create a booking object
//        Map<String, Object> booking = new HashMap<>();
//        booking.put("turfId", turfId);
//        booking.put("date", selectedDateText.getText().toString().substring(6)); // Extract date from the text
//        booking.put("timeSlot", timeSlot);
//        // Add more fields as necessary
//
//        db.collection("bookings").document(bookingId)
//                .set(booking)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(Schedule.this, "Booking Successful!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Schedule", "Error booking time slot: ", e);
//                    Toast.makeText(Schedule.this, "Failed to book time slot", Toast.LENGTH_SHORT).show();
//                });
//    }
//}
