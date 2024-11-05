package com.example.turfmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvitePlayer extends AppCompatActivity {

    private EditText emailInvite;
    private Button btnInvite;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String senderTeamId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_player);

        btnInvite = findViewById(R.id.btnInvite);
        emailInvite = findViewById(R.id.emailInvite);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchCurrentUserTeamId();

        fetchReceivedInvites();

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInvite.getText().toString().trim();
                if (!email.isEmpty()) {
                    findUserByEmail(email);
                } else {
                    Toast.makeText(InvitePlayer.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchCurrentUserTeamId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            db.collection("users").document(currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                senderTeamId = document.getString("teamId");
                                if (senderTeamId != null) {
                                    Toast.makeText(this, "Team ID found: " + senderTeamId, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "No team ID associated with the current user.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "User document does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error fetching team ID: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is currently signed in.", Toast.LENGTH_SHORT).show();
        }
    }

    public void findUserByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(InvitePlayer.this, "No user found with this email.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InvitePlayer.this, "User found with this email!", Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String receiverEmail = document.getString("email"); // Get receiver's email
                                sendInvite(mAuth.getCurrentUser().getEmail(), receiverEmail); // Call sendInvite with emails
                            }
                        }
                    } else {
                        Toast.makeText(InvitePlayer.this, "Error searching for user.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendInvite(String senderEmail, String receiverEmail) {
        if (senderTeamId == null) {
            Toast.makeText(this, "Sender team ID not found. Cannot send invite.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(InvitePlayer.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String status = "pending";
        long timestamp = System.currentTimeMillis();

        // Create invite data
        Map<String, Object> inviteData = new HashMap<>();
        inviteData.put("senderEmail", senderEmail);
        inviteData.put("receiverEmail", receiverEmail);
        inviteData.put("status", status);
        inviteData.put("teamId", senderTeamId);
        inviteData.put("timestamp", timestamp);

        db.collection("invites")
                .add(inviteData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(InvitePlayer.this, "Invite sent successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InvitePlayer.this, "Failed to send invite.", Toast.LENGTH_SHORT).show();
                    System.out.println("Error sending invite: " + e.getMessage());
                });
    }
    private void fetchReceivedInvites() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail();
        db.collection("invites")
                .whereEqualTo("receiverEmail", currentUserEmail)
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> receivedInvites = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> invite = document.getData();
                            invite.put("id", document.getId());
                            receivedInvites.add(invite);
                        }
                        setupReceivedInvitesRecyclerView(receivedInvites);
                    } else {
                        Toast.makeText(this, "Error fetching received invites.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupReceivedInvitesRecyclerView(List<Map<String, Object>> receivedInvites) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewReceivedRequests);
        ReceivedInviteAdapter adapter = new ReceivedInviteAdapter(receivedInvites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}