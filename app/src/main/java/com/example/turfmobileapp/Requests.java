package com.example.turfmobileapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {

    private RecyclerView requestRecyclerView;
    private RequestAdapter requestAdapter;
    private List<RequestItem> requestList; // Sample data model for requests

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        requestRecyclerView = findViewById(R.id.requestRecyclerView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize sample data
        requestList = new ArrayList<>();
        requestList.add(new RequestItem("Team A", "5 players", "10:00 AM"));
        requestList.add(new RequestItem("Team B", "7 players", "11:00 AM"));
        requestList.add(new RequestItem("Team C", "6 players", "12:00 PM"));

        // Initialize adapter and set it to RecyclerView
        requestAdapter = new RequestAdapter(requestList);
        requestRecyclerView.setAdapter(requestAdapter);
    }

    // Adapter class for RecyclerView
    private class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
        private List<RequestItem> requests;

        public RequestAdapter(List<RequestItem> requests) {
            this.requests = requests;
        }

        @NonNull
        @Override
        public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
            RequestItem request = requests.get(position);
            holder.teamNameText.setText(request.getTeamName());
            holder.playersText.setText(request.getNumberOfPlayers());
            holder.timeText.setText(request.getTime());

            holder.approveButton.setOnClickListener(v -> {
                // Handle approval logic here
                // Example: Remove the item from the list and notify the adapter
                requests.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, requests.size());
            });

            holder.declineButton.setOnClickListener(v -> {
                // Handle decline logic here
                // Example: Remove the item from the list and notify the adapter
                requests.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, requests.size());
            });
        }

        @Override
        public int getItemCount() {
            return requests.size();
        }

        // ViewHolder for each request item
        class RequestViewHolder extends RecyclerView.ViewHolder {
            TextView teamNameText, playersText, timeText;
            ImageView approveButton, declineButton;

            public RequestViewHolder(@NonNull View itemView) {
                super(itemView);
                teamNameText = itemView.findViewById(R.id.teamNameText);
                playersText = itemView.findViewById(R.id.playersText);
                timeText = itemView.findViewById(R.id.timeText);
                approveButton = itemView.findViewById(R.id.approveButton);
                declineButton = itemView.findViewById(R.id.declineButton);
            }
        }
    }

    // Sample data model class for requests
    private static class RequestItem {
        private String teamName;
        private String numberOfPlayers;
        private String time;

        public RequestItem(String teamName, String numberOfPlayers, String time) {
            this.teamName = teamName;
            this.numberOfPlayers = numberOfPlayers;
            this.time = time;
        }

        public String getTeamName() {
            return teamName;
        }

        public String getNumberOfPlayers() {
            return numberOfPlayers;
        }

        public String getTime() {
            return time;
        }
    }
}
