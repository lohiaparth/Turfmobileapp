package com.example.turfmobileapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTeam extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private String currentTeamId;
    private String teamLeaderId;
    private TextView teamIdTextView;
    private RecyclerView membersRecyclerView;
    private MembersAdapter membersAdapter;
    private List<Map<String, Object>> membersList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_team);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

//        teamIdTextView = findViewById(R.id.teamIdTextView);
        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize members list and adapter
        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(membersList, teamLeaderId);
        membersRecyclerView.setAdapter(membersAdapter);

        // Fetch current user's team ID
        fetchCurrentUserTeamId();
    }

    private void fetchCurrentUserTeamId() {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentTeamId = documentSnapshot.getString("teamId");
                        if (currentTeamId != null) {
//                            teamIdTextView.setText("Team ID: " + currentTeamId);
                            fetchTeamDetails(currentTeamId);
                        } else {
                            Toast.makeText(this, "You are not part of any team.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching team ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchTeamDetails(String teamId) {
        db.collection("teams").document(teamId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        teamLeaderId = documentSnapshot.getString("leaderId");
                        List<String> memberIds = (List<String>) documentSnapshot.get("members"); // Fetch list of member IDs
                        fetchMemberDetails(memberIds); // Fetch details for each member
                    } else {
                        Toast.makeText(this, "Team not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching team details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchMemberDetails(List<String> memberIds) {
        membersList.clear();

        for (String memberId : memberIds) {
            db.collection("users").document(memberId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Get the member's details
                            Map<String, Object> memberDetails = new HashMap<>();
                            memberDetails.put("name", documentSnapshot.getString("name"));
                            memberDetails.put("age", documentSnapshot.getString("age"));
                            memberDetails.put("contact", documentSnapshot.getString("contact"));
                            memberDetails.put("id", memberId); // Add ID for potential highlighting

                            // Add member details to the list
                            membersList.add(memberDetails);
                            membersAdapter.setTeamLeaderId(teamLeaderId); // Update adapter with leader ID
                            membersAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error fetching member details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}