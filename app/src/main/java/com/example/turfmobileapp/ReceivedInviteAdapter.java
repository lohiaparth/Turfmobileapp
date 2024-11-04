package com.example.turfmobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class ReceivedInviteAdapter extends RecyclerView.Adapter<ReceivedInviteAdapter.ReceivedInviteViewHolder> {

    private List<Map<String, Object>> receivedInvites;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public ReceivedInviteAdapter(List<Map<String, Object>> receivedInvites) {
        this.receivedInvites = receivedInvites;
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ReceivedInviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_invite, parent, false);
        return new ReceivedInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedInviteViewHolder holder, int position) {
        Map<String, Object> invite = receivedInvites.get(position);
        String senderEmail = (String) invite.get("senderEmail");
        String inviteId = (String) invite.get("id");

        holder.emailText.setText(senderEmail);

        holder.acceptButton.setOnClickListener(v -> acceptInvite(inviteId, senderEmail, v, position));
        holder.declineButton.setOnClickListener(v -> deleteInvite(inviteId, v, position));
    }

    @Override
    public int getItemCount() {
        return receivedInvites.size();
    }

    private void acceptInvite(String inviteId, String senderEmail, View view, int position) {
        // Step 1: Get the sender's team ID
        db.collection("users").whereEqualTo("email", senderEmail).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot senderDoc = querySnapshot.getDocuments().get(0);
                        String senderTeamId = senderDoc.getString("teamId");

                        if (senderTeamId != null) {
                            // Step 2: Update the current user's team ID
                            String currentUserId = mAuth.getCurrentUser().getUid();

                            db.collection("users").document(currentUserId)
                                    .update("teamId", senderTeamId, "isTeamLeader", false)
                                    .addOnSuccessListener(aVoid -> {
                                        // Step 3: Add current user to the sender's team's members array
                                        DocumentReference teamRef = db.collection("teams").document(senderTeamId);
                                        teamRef.update("members", FieldValue.arrayUnion(currentUserId))
                                                .addOnSuccessListener(aVoid2 -> {
                                                    Toast.makeText(view.getContext(), "Invite accepted and team updated", Toast.LENGTH_SHORT).show();
                                                    deleteInvite(inviteId, view, position); // Delete invite after acceptance
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(view.getContext(), "Failed to add user to team members", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(view.getContext(), "Failed to update user's team ID", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(view.getContext(), "Sender has no team assigned", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view.getContext(), "Sender not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Error fetching sender's team", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteInvite(String inviteId, View view, int position) {
        // Delete the invite document from Firestore
        db.collection("invites").document(inviteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(view.getContext(), "Invite deleted", Toast.LENGTH_SHORT).show();
                    // Remove the invite from the list and notify the adapter
                    receivedInvites.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Failed to delete invite.", Toast.LENGTH_SHORT).show();
                });
    }

    static class ReceivedInviteViewHolder extends RecyclerView.ViewHolder {
        TextView emailText;
        ImageButton acceptButton, declineButton;

        public ReceivedInviteViewHolder(@NonNull View itemView) {
            super(itemView);
            emailText = itemView.findViewById(R.id.emailText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}