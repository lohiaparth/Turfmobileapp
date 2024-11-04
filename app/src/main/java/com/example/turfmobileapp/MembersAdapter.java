package com.example.turfmobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private List<Map<String, Object>> membersList; // List of member details
    private String teamLeaderId; // ID of the team leader

    // Constructor
    public MembersAdapter(List<Map<String, Object>> membersList, String teamLeaderId) {
        this.membersList = membersList;
        this.teamLeaderId = teamLeaderId;
    }

    // Method to set or update the team leader ID
    public void setTeamLeaderId(String teamLeaderId) {
        this.teamLeaderId = teamLeaderId;
        notifyDataSetChanged(); // Refresh the list to highlight the leader
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Map<String, Object> memberDetails = membersList.get(position);

        // Display member details
        holder.nameTextView.setText("Name: " + memberDetails.get("name"));
        holder.ageTextView.setText("Age: " + memberDetails.get("age"));
        holder.contactTextView.setText("Contact: " + memberDetails.get("contact"));

        // Highlight team leader
        if (memberDetails.get("id").equals(teamLeaderId)) {
            holder.nameTextView.setText(holder.nameTextView.getText() + " (Leader)");
            holder.nameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.leaderHighlightColor));
        } else {
            holder.nameTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.defaultTextColor));
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ageTextView;
        TextView contactTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.memberNameTextView);
            ageTextView = itemView.findViewById(R.id.memberAgeTextView);
            contactTextView = itemView.findViewById(R.id.memberContactTextView);
        }
    }
}