package com.example.turfmobileapp;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class Payments extends AppCompatActivity {

    private Button btnCard, btnGpay, btnCash;
    private LinearLayout cardPaymentLayout, gpayPaymentLayout, cashPaymentLayout;

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

        // Set default visibility (e.g., default to "CARD" mode)
        showCardPayment();

        // Set onClick listeners for each button to toggle layouts
        btnCard.setOnClickListener(v -> showCardPayment());
        btnGpay.setOnClickListener(v -> showGpayPayment());
        btnCash.setOnClickListener(v -> showCashPayment());
    }

    // Method to show the card payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showCardPayment() {
        cardPaymentLayout.setVisibility(View.VISIBLE);
        gpayPaymentLayout.setVisibility(View.GONE);
        cashPaymentLayout.setVisibility(View.GONE);

        // Update button styles (optional - to indicate selection)
        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.selectedButtonColor));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
    }

    // Method to show the GPay payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showGpayPayment() {
        cardPaymentLayout.setVisibility(View.GONE);
        gpayPaymentLayout.setVisibility(View.VISIBLE);
        cashPaymentLayout.setVisibility(View.GONE);

        // Update button styles
        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.selectedButtonColor));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
    }

    // Method to show the cash payment layout
    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showCashPayment() {
        cardPaymentLayout.setVisibility(View.GONE);
        gpayPaymentLayout.setVisibility(View.GONE);
        cashPaymentLayout.setVisibility(View.VISIBLE);

        // Update button styles
        btnCard.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
        btnGpay.setBackgroundTintList(getResources().getColorStateList(R.color.defaultButtonColor));
        btnCash.setBackgroundTintList(getResources().getColorStateList(R.color.selectedButtonColor));
    }
}
