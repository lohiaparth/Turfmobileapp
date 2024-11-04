package com.example.turfmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListTeam extends AppCompatActivity {

    private Button listmyteam_btn, submit_btn, createteam_btn, jointeam_btn;
    private EditText team_name_input;
    private FirebaseFirestore db;

    // Assuming these are the user's current team ID and leader status for demonstration purposes
    private String currentTeamId; // This should be fetched from the user's data in Firestore
    private boolean isTeamLeader;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_team);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        listmyteam_btn = findViewById(R.id.listmyteam_btn);
        submit_btn = findViewById(R.id.submit_btn);
        createteam_btn = findViewById(R.id.createteam_btn);
        jointeam_btn = findViewById(R.id.jointeam_btn);
        team_name_input = findViewById(R.id.team_name_input);

        // Initially hide the submit button and team name input
        submit_btn.setVisibility(View.INVISIBLE);
        team_name_input.setVisibility(View.INVISIBLE);

        // Load user's team status from Firestore
        loadUserTeamStatus();

        // Set click listener for create team button
        createteam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show input field for team name
                submit_btn.setVisibility(View.VISIBLE);
                team_name_input.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for submit button
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teamName = team_name_input.getText().toString().trim();
                if (!teamName.isEmpty()) {
                    createTeam(teamName);
                } else {
                    Toast.makeText(ListTeam.this, "Please enter a team name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for join team button
        jointeam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement functionality to join an existing team
                // This can include showing a dialog or another activity to search for teams
                Toast.makeText(ListTeam.this, "Join Team functionality to be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for list my team button
        listmyteam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the user's team details
                if (currentTeamId != null) {
                    // Fetch and display team details from Firestore
                    fetchTeamDetails(currentTeamId);
                } else {
                    Toast.makeText(ListTeam.this, "You are not part of any team", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserTeamStatus() {
        // Fetch user data from Firestore (assumed structure)

        String userId = currentUser.getUid(); // Replace with the actual user ID

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentTeamId = documentSnapshot.getString("teamId");
//                        isTeamLeader = documentSnapshot.getBoolean("isTeamLeader");

                        // Update UI based on user's team status
                        if (currentTeamId != null) {
                            // User is already part of a team
                            listmyteam_btn.setVisibility(View.VISIBLE); // Show list team button
                            createteam_btn.setEnabled(false); // Disable create team button
                            createteam_btn.setVisibility(View.INVISIBLE); // Hide the create team button
                            team_name_input.setVisibility(View.INVISIBLE); // Hide team name input
                            submit_btn.setVisibility(View.INVISIBLE); // Hide submit button
                        } else {
                            // User is not part of a team
                            createteam_btn.setVisibility(View.VISIBLE); // Show create team button
                            jointeam_btn.setVisibility(View.VISIBLE); // Show join team button
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ListTeam.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createTeam(String teamName) {
        // Logic to create a new team
        String teamId = "team_" + UUID.randomUUID().toString(); // Generate team ID
        // Prepare team data
        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName);
        teamData.put("leaderId", "currentUserId"); // Replace with actual user ID
        teamData.put("members", new ArrayList<>()); // Initialize with empty list
        teamData.put("createdAt", FieldValue.serverTimestamp());

        // Add team to Firestore

        db.collection("teams").document(teamId).set(teamData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListTeam.this, "Team created successfully!", Toast.LENGTH_SHORT).show();
                    // Update user's team information
                    updateUserTeamInfo(currentUser.getUid(), teamId); // currentUserId
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ListTeam.this, "Error creating team: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserTeamInfo(String userId, String teamId) {
        // Update user's team information in Firestore
        db.collection("users").document(userId).update("teamId", teamId, "isTeamLeader", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListTeam.this, "User updated with team information!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ListTeam.this, "Error updating user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchTeamDetails(String teamId) {
        // Fetch team details from Firestore
        db.collection("teams").document(teamId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract team details and display them
                        String teamName = documentSnapshot.getString("teamName");
                        // Show team details (you might want to implement a new Activity or dialog)
                        Toast.makeText(ListTeam.this, "Team Name: " + teamName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListTeam.this, "Team does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ListTeam.this, "Error fetching team details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}