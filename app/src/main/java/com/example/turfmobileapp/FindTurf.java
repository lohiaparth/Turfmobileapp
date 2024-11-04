package com.example.turfmobileapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class FindTurf extends AppCompatActivity {

    private EditText searchEditText;
    private ImageView toggleImageView;

    private static final String TAG = "ViewTurfsActivity";

    private RecyclerView turfsRecyclerView;
    private TurfsAdapter turfsAdapter;
    private List<Turf> turfsList;

    private FirebaseFirestore db;
    private CollectionReference turfsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_turf);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        turfsCollection = db.collection("listturfs");

        // Initialize RecyclerView
        turfsRecyclerView = findViewById(R.id.turfsRecyclerView);
        turfsRecyclerView.setHasFixedSize(true);
        turfsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        turfsList = new ArrayList<>();
        turfsAdapter = new TurfsAdapter(this, turfsList);
        turfsRecyclerView.setAdapter(turfsAdapter);

        // Fetch turfs from Firestore
        fetchTurfs();
        searchEditText = findViewById(R.id.searchEditText);
        toggleImageView = findViewById(R.id.toggleImageView);

        toggleImageView.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim().toLowerCase();
            filterTurfs(query);
        });
    }

    private void filterTurfs(String query) {
        List<Turf> filteredList = new ArrayList<>();
        for (Turf turf : turfsList) {
            if (turf.getCompanyName().toLowerCase().contains(query) ||
                    turf.getAddress().toLowerCase().contains(query)) {
                filteredList.add(turf);
            }
        }
        turfsAdapter.setTurfsList(filteredList);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchTurfs() {
        turfsCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        turfsList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Turf turf = document.toObject(Turf.class);
                                turf.setId(document.getId()); // Set the document ID
                                turfsList.add(turf);
                            }
                            turfsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(FindTurf.this, "No turfs found.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No turfs found.");
                        }
                    } else {
                        Toast.makeText(FindTurf.this, "Error fetching turfs.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error fetching turfs: ", task.getException());
                    }
                });
    }
}
