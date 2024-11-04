package com.example.turfmobileapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindTurf extends AppCompatActivity {

    private EditText searchEditText;
    private ImageView toggleImageView;
    private Button areaFilterButton, sportsFilterButton, priceFilterButton;
    private RecyclerView turfsRecyclerView;
    private TurfsAdapter turfsAdapter;
    private List<Turf> turfsList;
    private Set<String> selectedAreas = new HashSet<>();
    private Set<String> selectedSports = new HashSet<>();
    private Set<String> selectedPriceRanges = new HashSet<>();
    private FirebaseFirestore db;
    private CollectionReference turfsCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_turf);

        db = FirebaseFirestore.getInstance();
        turfsCollection = db.collection("listturfs");

        turfsRecyclerView = findViewById(R.id.turfsRecyclerView);
        turfsRecyclerView.setHasFixedSize(true);
        turfsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        turfsList = new ArrayList<>();
        turfsAdapter = new TurfsAdapter(this, turfsList);
        turfsRecyclerView.setAdapter(turfsAdapter);

        fetchTurfs();

        searchEditText = findViewById(R.id.searchEditText);
        toggleImageView = findViewById(R.id.toggleImageView);
        areaFilterButton = findViewById(R.id.areaFilterButton);
        sportsFilterButton = findViewById(R.id.sportsFilterButton);
        priceFilterButton = findViewById(R.id.priceFilterButton);

        toggleImageView.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim().toLowerCase();
            filterTurfs(query);
        });

        areaFilterButton.setOnClickListener(v -> showAreaFilterDialog());
        sportsFilterButton.setOnClickListener(v -> showSportsFilterDialog());
        //priceFilterButton.setOnClickListener(v -> showPriceFilterDialog());
    }

    private void showAreaFilterDialog() {
        String[] areas = {"Andheri", "Juhu", "Malad West", "Kandivali"};
        boolean[] checkedItems = new boolean[areas.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Areas")
                .setMultiChoiceItems(areas, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) selectedAreas.add(areas[which]);
                    else selectedAreas.remove(areas[which]);
                })
                .setPositiveButton("OK", (dialog, which) -> filterTurfsBySelection())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSportsFilterDialog() {
        String[] sports = {"All", "Cricket", "Football"};
        boolean[] checkedItems = new boolean[sports.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Sports")
                .setMultiChoiceItems(sports, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) selectedSports.add(sports[which]);
                    else selectedSports.remove(sports[which]);
                })
                .setPositiveButton("OK", (dialog, which) -> filterTurfsBySelection())
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void filterTurfsBySelection() {
        List<Turf> filteredList = new ArrayList<>();
        for (Turf turf : turfsList) {
            boolean matchesArea = selectedAreas.isEmpty() || selectedAreas.contains(turf.getAddress());
            boolean matchesSport = selectedSports.isEmpty() || selectedSports.contains(turf.getSport());
            boolean matchesPrice = selectedPriceRanges.isEmpty() || selectedPriceRanges.contains(turf.getPrice());

            if (matchesArea && matchesSport && matchesPrice) {
                filteredList.add(turf);
            }
        }
        turfsAdapter.setTurfsList(filteredList);
    }

    private void filterTurfs(String query) {
        List<Turf> filteredList = new ArrayList<>();
        for (Turf turf : turfsList) {
            if (turf.getCompanyName().toLowerCase().contains(query) || turf.getAddress().toLowerCase().contains(query)) {
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
                                turf.setId(document.getId());
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
