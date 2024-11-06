package com.example.turfmobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ListTurf extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;
    private static final String TAG = "ListTurf";

    private EditText companyName, email, contact, address, sport, price;
    private Button uploadImageButton, btnListTurf;
    private ImageView gmap_btn;
    private TextView imageText;

    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_turf);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("turfs_images");

        // Initialize UI components
        initializeUI();

        // Image upload button
        uploadImageButton.setOnClickListener(v -> openFileChooser());

        // List turf button
        btnListTurf.setOnClickListener(v -> handleListTurf());

        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCpls-0Mx7mdXpvDUivTADm4d5zqiFW-yI"); // Replace with your API key
        }

        // Set OnClickListener for gmap_btn to trigger address search
        gmap_btn.setOnClickListener(v -> openPlaceAutocomplete());
    }

    private void initializeUI() {
        companyName = findViewById(R.id.companyName);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address); // This field will show the selected address
        sport = findViewById(R.id.sport);
        price = findViewById(R.id.price);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        btnListTurf = findViewById(R.id.btnListTurf);
        imageText = findViewById(R.id.imageText);
        gmap_btn = findViewById(R.id.gmap_btn); // ImageView for map icon
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openPlaceAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String selectedAddress = place.getAddress();

                // Set the selected address in the address EditText
                address.setText(selectedAddress);

                Log.i(TAG, "Place selected: " + place.getName() + ", Address: " + selectedAddress);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, "Error selecting place: " + (status != null ? status.getStatusMessage() : "Unknown error"));
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageText.setText("Image Selected");
            Log.d(TAG, "Image URI: " + imageUri.toString());
        }
    }

    private void handleListTurf() {
        String companyNameText = companyName.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String contactText = contact.getText().toString().trim();
        String addressText = address.getText().toString().trim();
        String sportText = sport.getText().toString().trim();
        String priceText = price.getText().toString().trim();

        if (companyNameText.isEmpty() || emailText.isEmpty() || contactText.isEmpty() ||
                addressText.isEmpty() || sportText.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImageToStorage(companyNameText, emailText, contactText, addressText, sportText, priceText);
        } else {
            saveTurfData(companyNameText, emailText, contactText, addressText, sportText, priceText, null);
        }
    }

    private void uploadImageToStorage(String companyName, String email, String contact, String address, String sport, String price) {
        StorageReference fileRef = storageReference.child(UUID.randomUUID().toString());

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Log.d(TAG, "Image uploaded successfully, URL: " + imageUrl);
                    saveTurfData(companyName, email, contact, address, sport, price, imageUrl);
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: " + e.getMessage());
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveTurfData(String companyName, String email, String contact, String address, String sport, String price, @Nullable String imageUrl) {
        Map<String, Object> turf = new HashMap<>();
        turf.put("companyName", companyName);
        turf.put("email", email);
        turf.put("contact", contact);
        turf.put("address", address);
        turf.put("sport", sport);
        turf.put("price", price);

        if (imageUrl != null) {
            turf.put("image", imageUrl);
        }

        db.collection("listturfs")
                .add(turf)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ListTurf.this, "Turf Listed Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Turf data saved with ID: " + documentReference.getId());
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to list turf: " + e.getMessage());
                    Toast.makeText(ListTurf.this, "Failed to list turf: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        companyName.setText("");
        email.setText("");
        contact.setText("");
        address.setText("");
        sport.setText("");
        price.setText("");
        imageText.setText("Image");
        imageUri = null;
    }
}