package com.example.turfmobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListTeam extends AppCompatActivity {

    private Button listMyTeamBtn, submitBtn, createTeamBtn, joinTeamBtn, abandonTeamBtn, searchTeamBtn;
    private EditText teamNameInput;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String currentTeamId; // User's current team ID
    private boolean isTeamLeader; // Indicates if the user is a team leader

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_team);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        submitBtn = findViewById(R.id.submit_btn);
        createTeamBtn = findViewById(R.id.createteam_btn);
        joinTeamBtn = findViewById(R.id.jointeam_btn);
        abandonTeamBtn = findViewById(R.id.abandonteam_btn); // New button for abandoning or disbanding team
        searchTeamBtn = findViewById(R.id.search_team_btn); // Button for searching a team
        teamNameInput = findViewById(R.id.team_name_input);

        // Initially hide the submit button and team name input
        submitBtn.setVisibility(View.INVISIBLE);
        searchTeamBtn.setVisibility(View.INVISIBLE);
        teamNameInput.setVisibility(View.INVISIBLE);

        // Load user's team status from Firestore
        loadUserTeamStatus();

        // Set click listeners
        createTeamBtn.setOnClickListener(view -> showTeamNameInput());
        submitBtn.setOnClickListener(view -> submitTeamName());
        joinTeamBtn.setOnClickListener(view -> showTeamSearchInput());
        abandonTeamBtn.setOnClickListener(view -> abandonTeam());
        searchTeamBtn.setOnClickListener(view -> searchTeamName()); // Set click listener for searching a team
    }

    private void loadUserTeamStatus() {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentTeamId = documentSnapshot.getString("teamId"); // Load user's team ID
                        isTeamLeader = Boolean.TRUE.equals(documentSnapshot.getBoolean("isTeamLeader")); // Load if the user is a team leader
                        updateUIBasedOnTeamStatus(); // Update UI based on whether the user is in a team and their role
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUIBasedOnTeamStatus() {
        if (currentTeamId != null) {
            // User is part of a team
            createTeamBtn.setEnabled(false);
            joinTeamBtn.setVisibility(View.INVISIBLE);
            createTeamBtn.setVisibility(View.INVISIBLE);
            teamNameInput.setVisibility(View.INVISIBLE);
            submitBtn.setVisibility(View.INVISIBLE);
            searchTeamBtn.setVisibility(View.INVISIBLE);

            // Change abandon button's text based on whether the user is a leader or member
            if (isTeamLeader) {
                abandonTeamBtn.setText("Disband Team"); // Team leader can disband the team
            } else {
                abandonTeamBtn.setText("Leave Team"); // Regular members can leave the team
            }
        } else {
            // User is not part of a team
            createTeamBtn.setEnabled(true);
            createTeamBtn.setVisibility(View.VISIBLE);
            joinTeamBtn.setVisibility(View.VISIBLE);
            abandonTeamBtn.setVisibility(View.INVISIBLE);// Hide abandon button if not part of any team
        }
    }

    private void showTeamNameInput() {
        // Show input field for team name and submit button
        submitBtn.setVisibility(View.VISIBLE);
        teamNameInput.setVisibility(View.VISIBLE);
    }
    private void showTeamSearchInput() {
        // Show input field for team name and submit button
        searchTeamBtn.setVisibility(View.VISIBLE);
        teamNameInput.setVisibility(View.VISIBLE);
    }



    private void submitTeamName() {
        String teamName = teamNameInput.getText().toString().trim();
        if (!teamName.isEmpty()) {
            createTeam(teamName); // Create team with specified name
        } else {
            Toast.makeText(ListTeam.this, "Please enter a team name", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchTeamName() {
        String teamName = teamNameInput.getText().toString().trim();
        if (teamName.isEmpty()) {
            Toast.makeText(ListTeam.this, "Please enter a team name", Toast.LENGTH_SHORT).show();
        } else {
            db.collection("teams")
                    .whereEqualTo("teamName", teamName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Team found, get the leaderId
                            DocumentSnapshot teamDoc = task.getResult().getDocuments().get(0);
                            String leaderId = teamDoc.getString("leaderId");

                            if (leaderId != null) {
                                // Query Firestore to get the leader's email using the leaderId
                                db.collection("users").document(leaderId)
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                                                String leaderEmail = userTask.getResult().getString("email");

                                                if (leaderEmail != null) {
                                                    // Send an email to the leader requesting to join the team
                                                    sendJoinRequestEmail(leaderEmail, teamName);
                                                } else {
                                                    Toast.makeText(this, "Leader's email not found", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(this, "Failed to retrieve leader's details", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(this, "Leader ID not found for this team", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Team not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(ListTeam.this, "Error searching for team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void sendJoinRequestEmail(String leaderEmail, String teamName) {
        // Create an intent to open an email app with pre-filled email details
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + leaderEmail));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request to Join Team: " + teamName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI would like to join your team \"" + teamName + "\".\n\nThank you!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
            Toast.makeText(this, "Join request email sent", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createTeam(String teamName) {
        String teamId = "team_" + UUID.randomUUID().toString(); // Generate unique team ID

        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName); // Set team name
        teamData.put("leaderId", currentUser.getUid()); // Set current user as team leader
        teamData.put("members", new ArrayList<>(List.of(currentUser.getUid()))); // Initialize with leader ID as member
        teamData.put("createdAt", FieldValue.serverTimestamp()); // Set creation timestamp

        db.collection("teams").document(teamId).set(teamData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListTeam.this, "Team created successfully!", Toast.LENGTH_SHORT).show();
                    updateUserTeamInfo(currentUser.getUid(), teamId);// Update user's team information with the new team ID
                    Intent intent = new Intent(ListTeam.this, ViewTeam.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error creating team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUserTeamInfo(String userId, String teamId) {
        // Set user's teamId and isTeamLeader status to true in Firestore
        db.collection("users").document(userId).update("teamId", teamId, "isTeamLeader", true)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ListTeam.this, "User updated with team information!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error updating user information: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void abandonTeam() {
        if (isTeamLeader) {
            disbandTeam();
        } else {
            leaveTeam();
        }
    }

    private void disbandTeam() {
        db.collection("teams").document(currentTeamId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    updateUserTeamInfoAfterAbandon();
                    Toast.makeText(ListTeam.this, "Team disbanded successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error disbanding team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void leaveTeam() {
        updateUserTeamInfoAfterAbandon();
        Toast.makeText(ListTeam.this, "Left the team successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateUserTeamInfoAfterAbandon() {
        db.collection("users").document(currentUser.getUid()).update("teamId", null, "isTeamLeader", false)
                .addOnSuccessListener(aVoid -> {
                    currentTeamId = null;
                    isTeamLeader = false;
                    updateUIBasedOnTeamStatus();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error updating user information after leaving team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}