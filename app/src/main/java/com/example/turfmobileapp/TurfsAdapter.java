// TurfsAdapter.java
package com.example.turfmobileapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import other necessary packages
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class TurfsAdapter extends RecyclerView.Adapter<TurfsAdapter.TurfViewHolder> {

    private Context mContext;
    private List<Turf> turfsList;

    public TurfsAdapter(Context context, List<Turf> turfs) {
        this.mContext = context;
        this.turfsList = turfs;
    }

    @NonNull
    @Override
    public TurfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_turf, parent, false);
        return new TurfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurfViewHolder holder, int position) {
        Turf turf = turfsList.get(position);
        holder.turfNameText.setText(turf.getCompanyName());
        holder.sportsText.setText("Sports: " + turf.getSport());
        holder.locationText.setText("Location: " + turf.getAddress());
        holder.priceText.setText("Price: $" + turf.getPrice());

        // Load image using Glide
        if (turf.getImage() != null && !turf.getImage().isEmpty()) {
            Glide.with(mContext)
                    .load(turf.getImage())
                    .placeholder(R.drawable.turf)
                    .into(holder.turfImageView);
        } else {
            holder.turfImageView.setImageResource(R.drawable.turf);
        }

        // Handle Book Now button click
        holder.buttonBookNow.setOnClickListener(v -> {
            // Implement booking functionality
            // For example, open a booking activity with turf details
            Intent intent = new Intent(mContext, BookingPage.class);
            intent.putExtra("turfId", turf.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return turfsList.size();
    }

    public void setTurfsList(List<Turf> turfs) {
        this.turfsList = turfs;
        notifyDataSetChanged();
    }

    public static class TurfViewHolder extends RecyclerView.ViewHolder {
        ImageView turfImageView;
        TextView turfNameText, sportsText, locationText, priceText;
        Button buttonBookNow;

        public TurfViewHolder(@NonNull View itemView) {
            super(itemView);
            turfImageView = itemView.findViewById(R.id.turfImageView);
            turfNameText = itemView.findViewById(R.id.turfNameText);
            sportsText = itemView.findViewById(R.id.sportsText);
            locationText = itemView.findViewById(R.id.locationText);
            priceText = itemView.findViewById(R.id.priceText);
            buttonBookNow = itemView.findViewById(R.id.buttonBookNow);
        }
    }
}
