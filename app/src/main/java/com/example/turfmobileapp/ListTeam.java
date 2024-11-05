package com.example.turfmobileapp;

import android.content.Intent;
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

    private Button listMyTeamBtn, submitBtn, createTeamBtn, joinTeamBtn, abandonTeamBtn;
    private EditText teamNameInput;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String currentTeamId;
    private boolean isTeamLeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_team);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

//        listMyTeamBtn = findViewById(R.id.listmyteam_btn);
        submitBtn = findViewById(R.id.submit_btn);
        createTeamBtn = findViewById(R.id.createteam_btn);
        joinTeamBtn = findViewById(R.id.jointeam_btn);
        abandonTeamBtn = findViewById(R.id.abandonteam_btn);
        teamNameInput = findViewById(R.id.team_name_input);

        submitBtn.setVisibility(View.INVISIBLE);
        teamNameInput.setVisibility(View.INVISIBLE);

        loadUserTeamStatus();

        createTeamBtn.setOnClickListener(view -> showTeamNameInput());
        submitBtn.setOnClickListener(view -> submitTeamName());
        joinTeamBtn.setOnClickListener(view -> joinTeam());
//        listMyTeamBtn.setOnClickListener(view -> listMyTeam());
        abandonTeamBtn.setOnClickListener(view -> abandonTeam()); // Set click listener for abandon team button
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
//            listMyTeamBtn.setVisibility(View.VISIBLE);
            createTeamBtn.setEnabled(false);
            createTeamBtn.setVisibility(View.INVISIBLE);
            teamNameInput.setVisibility(View.INVISIBLE);
            submitBtn.setVisibility(View.INVISIBLE);

            if (isTeamLeader) {
                abandonTeamBtn.setText("Disband Team"); // Team leader can disband the team
            } else {
                abandonTeamBtn.setText("Leave Team"); // Regular members can leave the team
            }
        } else {
            createTeamBtn.setEnabled(true);
            createTeamBtn.setVisibility(View.VISIBLE);
            joinTeamBtn.setVisibility(View.VISIBLE);
            abandonTeamBtn.setVisibility(View.INVISIBLE); // Hide abandon button if not part of any team
        }
    }

    private void showTeamNameInput() {
        submitBtn.setVisibility(View.VISIBLE);
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

    private void joinTeam() {
        // Placeholder for joining team functionality
        Toast.makeText(ListTeam.this, "Join Team functionality to be implemented", Toast.LENGTH_SHORT).show();
    }

    private void listMyTeam() {
        loadUserTeamStatus();
        if (currentTeamId != null) {
            fetchTeamDetails(currentTeamId); // Show team details if user is part of a team
        } else {
            Toast.makeText(ListTeam.this, "You are not part of any team", Toast.LENGTH_SHORT).show();
        }
    }

    private void createTeam(String teamName) {
        String teamId = "team_" + UUID.randomUUID().toString(); // Generate unique team ID

        Map<String, Object> teamData = new HashMap<>();
        teamData.put("teamName", teamName); // Set team name
        teamData.put("leaderId", currentUser.getUid()); // Set current user as team leader
        teamData.put("members", new ArrayList<>(List.of(currentUser.getUid()))); // Initialize with empty members list
        teamData.put("createdAt", FieldValue.serverTimestamp()); // Set creation timestamp

        db.collection("teams").document(teamId).set(teamData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ListTeam.this, "Team created successfully!", Toast.LENGTH_SHORT).show();
                    updateUserTeamInfo(currentUser.getUid(), teamId);// Update user's team information with the new team ID
                    Intent intent = new Intent(ListTeam.this,ViewTeam.class);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error creating team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUserTeamInfo(String userId, String teamId) {
        db.collection("users").document(userId).update("teamId", teamId, "isTeamLeader", true)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ListTeam.this, "User updated with team information!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error updating user information: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchTeamDetails(String teamId) {
        db.collection("teams").document(teamId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String teamName = documentSnapshot.getString("teamName"); // Fetch and display team name
                        Toast.makeText(ListTeam.this, "Team Name: " + teamName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListTeam.this, "Team does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error fetching team details: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void abandonTeam() {
        // Check if the user is a team leader to decide between disbanding or leaving
        if (isTeamLeader) {
            disbandTeam(); // Team leader disbands team
        } else {
            leaveTeam(); // Regular member leaves the team
        }
    }

    private void disbandTeam() {
        // Delete team document from Firestore if team leader
        db.collection("teams").document(currentTeamId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    updateUserTeamInfoAfterAbandon(); // Reset user's team information
                    Toast.makeText(ListTeam.this, "Team disbanded successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error disbanding team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void leaveTeam() {
        updateUserTeamInfoAfterAbandon(); // Reset user's team information
        Toast.makeText(ListTeam.this, "Left the team successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateUserTeamInfoAfterAbandon() {
        // Remove user's teamId and reset isTeamLeader to false in Firestore
        db.collection("users").document(currentUser.getUid()).update("teamId", null, "isTeamLeader", false)
                .addOnSuccessListener(aVoid -> {
                    currentTeamId = null; // Reset team ID locally
                    isTeamLeader = false; // Reset team leader status locally
                    updateUIBasedOnTeamStatus(); // Update UI accordingly
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ListTeam.this, "Error updating user information after leaving team: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}