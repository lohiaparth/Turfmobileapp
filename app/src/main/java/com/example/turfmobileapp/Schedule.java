package com.example.turfmobileapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Schedule extends AppCompatActivity {

    private TextView selectedDateText;
    private Button selectDateButton;
    private GridLayout timetableGridLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule);

        // Initialize UI elements
        selectedDateText = findViewById(R.id.selectedDateText);
        selectDateButton = findViewById(R.id.selectDateButton);
        timetableGridLayout = findViewById(R.id.timetableGridLayout);

        // Set up Date Picker dialog on select date button
        selectDateButton.setOnClickListener(view -> showDatePickerDialog());

        // Load today's bookings by default
        loadTimetableForDate(Calendar.getInstance());
    }

    private void showDatePickerDialog() {
        // Get today's date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Display DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);

                    // Update selected date text
                    String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateText.setText("Date: " + dateString);

                    // Load timetable for the selected date
                    loadTimetableForDate(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void loadTimetableForDate(Calendar date) {
        // Placeholder: Clear existing timetable entries
        for (int i = 1; i < timetableGridLayout.getChildCount(); i += 2) {
            TextView timeSlot = (TextView) timetableGridLayout.getChildAt(i);
            timeSlot.setText("Available"); // Reset to "Available"
        }

        // TODO: Retrieve bookings from your database for the selected date
        // For each booked slot, update the timetable with the team name and time

        // Example booking update
        TextView slot_9_10 = findViewById(R.id.slot_9_10);
        slot_9_10.setText("Team Alpha"); // Example team name

        Toast.makeText(this, "Timetable updated for selected date", Toast.LENGTH_SHORT).show();
    }
}
