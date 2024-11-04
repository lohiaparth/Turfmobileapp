package com.example.turfmobileapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;  // Import TextView for displaying amount
import androidx.appcompat.app.AppCompatActivity;

public class Payments extends AppCompatActivity {

    private Button btnCard, btnGpay, btnCash;
    private LinearLayout cardPaymentLayout, gpayPaymentLayout, cashPaymentLayout;
    private TextView amountTextView, dateTextView, timeTextView; // TextViews to display the data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payments);

        // Initialize buttons
        btnCard = findViewById(R.id.btnCard);
        btnGpay = findViewById(R.id.btnGpay);
        btnCash = findViewById(R.id.btnCash);

        // Initialize layouts for each payment mode
        cardPaymentLayout = findViewById(R.id.cardPaymentLayout);
        gpayPaymentLayout = findViewById(R.id.gpayPaymentLayout);
        cashPaymentLayout = findViewById(R.id.cashPaymentLayout);

        amountTextView = findViewById(R.id.amountTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);

        // Retrieve intent data from BookingPage
        retrieveIntentData();

        // Set default visibility (e.g., default to "CARD" mode)
        showCardPayment();

        // Set onClick listeners for each button to toggle layouts
        btnCard.setOnClickListener(v -> showCardPayment());
        btnGpay.setOnClickListener(v -> showGpayPayment());
        btnCash.setOnClickListener(v -> showCashPayment());
    }

    // Method to retrieve data from the intent
    private void retrieveIntentData() {
        String finalAmount = getIntent().getStringExtra("finalAmount");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");

        // Set the text for TextViews to display the received data
        if (finalAmount != null) {
            amountTextView.setText(finalAmount);
        }
        if (date != null) {
            dateTextView.setText(date);
        }
        if (time != null) {
            timeTextView.setText(time);
        }
    }

    // Method to show the card payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showCardPayment() {
        cardPaymentLayout.setVisibility(View.VISIBLE);
        gpayPaymentLayout.setVisibility(View.GONE);
        cashPaymentLayout.setVisibility(View.GONE);

        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.darkgreen));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.green));
    }

    // Method to show the GPay payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showGpayPayment() {
        cardPaymentLayout.setVisibility(View.GONE);
        gpayPaymentLayout.setVisibility(View.VISIBLE);
        cashPaymentLayout.setVisibility(View.GONE);

        // Update button styles
        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.darkgreen));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.green));
    }

    // Method to show the cash payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showCashPayment() {
        cardPaymentLayout.setVisibility(View.GONE);
        gpayPaymentLayout.setVisibility(View.GONE);
        cashPaymentLayout.setVisibility(View.VISIBLE);

        // Update button styles
        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.darkgreen));
    }
}
