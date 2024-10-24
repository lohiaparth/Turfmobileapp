package com.example.turfmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button inviteplayer_btn, viewteam_btn, findturf_btn, listteam_btn; // USER BUTTONS
    private Button listturf_btn, requests_btn, schedule_btn, payments_btn; // OWNER BUTTONS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the userType from the Intent
        Intent intent = getIntent();
        String userType = intent.getStringExtra("userType");

        initializeButtons(); // Initialize buttons
        setupUI(userType); // Setup UI based on userType

        // Set OnClickListeners for buttons
        setButtonListeners();
    }

    private void initializeButtons() {
        inviteplayer_btn = findViewById(R.id.inviteplayer_btn);
        viewteam_btn = findViewById(R.id.viewteam_btn);
        findturf_btn = findViewById(R.id.findturf_btn);
        listteam_btn = findViewById(R.id.listmyteam_btn);
        listturf_btn = findViewById(R.id.listturf_btn);
        requests_btn = findViewById(R.id.request_btn);
        schedule_btn = findViewById(R.id.schedule_btn);
        payments_btn = findViewById(R.id.payments_btn);
    }

    private void setupUI(String userType) {
        if ("Owner".equals(userType)) {
            setupButtonsVisibility(true);
        } else {
            setupButtonsVisibility(false);
        }
    }

    private void setupButtonsVisibility(boolean isOwner) {
        int ownerVisibility = isOwner ? View.VISIBLE : View.INVISIBLE;
        int userVisibility = isOwner ? View.INVISIBLE : View.VISIBLE;

        listturf_btn.setVisibility(ownerVisibility);
        schedule_btn.setVisibility(ownerVisibility);
        payments_btn.setVisibility(ownerVisibility);
        requests_btn.setVisibility(ownerVisibility);

        inviteplayer_btn.setVisibility(userVisibility);
        viewteam_btn.setVisibility(userVisibility);
        findturf_btn.setVisibility(userVisibility);
        listteam_btn.setVisibility(userVisibility);
    }

    private void setButtonListeners() {
        listturf_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ListTurf.class)));
        requests_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Requests.class)));
        schedule_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Schedule.class)));
        payments_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, Payments.class)));
        inviteplayer_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, InvitePlayer.class)));
        viewteam_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ViewTeam.class)));
        findturf_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, FindTurf.class)));
        listteam_btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ListTeam.class)));
    }
}