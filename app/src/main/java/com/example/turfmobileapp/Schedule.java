package com.example.turfmobileapp;

import android.app.DatePickerDialog;
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
//            case "12:00":
//                ((TextView) findViewById(R.id.slot_12_1)).setText(teamName);
//                break;
//            case "1:00":
//                ((TextView) findViewById(R.id.slot_1_2)).setText(teamName);
//                break;
//            case "2:00":
//                ((TextView) findViewById(R.id.slot_2_3)).setText(teamName);
//                break;
//            case "3:00":
//                ((TextView) findViewById(R.id.slot_3_4)).setText(teamName);
//                break;
//            case "4:00":
//                ((TextView) findViewById(R.id.slot_4_5)).setText(teamName);
//                break;
//            case "5:00":
//                ((TextView) findViewById(R.id.slot_5_6)).setText(teamName);
//                break;
            default:
                Log.w("Schedule", "Unexpected time slot: " + timeSlot);
                break;
        }
    }
}
