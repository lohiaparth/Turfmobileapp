//package com.example.turfmobileapp;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class ListTurf extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final String TAG = "ListTurf";
//
//    private EditText companyName, email, contact, address, sport, price;
//    private Button uploadImageButton, btnListTurf;
//    private TextView imageText;
//
//    private Uri imageUri;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//    private StorageReference storageReference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list_turf);
//
//        // Initialize Firebase Auth and Firestore
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        storageReference = FirebaseStorage.getInstance().getReference("turfs_images");
//
//        // Initialize UI components
//        initializeUI();
//
//        // Image upload button
//        uploadImageButton.setOnClickListener(v -> openFileChooser());
//
//        // List turf button
//        btnListTurf.setOnClickListener(v -> handleListTurf());
//    }
//
//
//    private void initializeUI() {
//        companyName = findViewById(R.id.companyName);
//        email = findViewById(R.id.email);
//        contact = findViewById(R.id.contact);
//        address = findViewById(R.id.address);
//        sport = findViewById(R.id.sport);
//        price = findViewById(R.id.price);
//        uploadImageButton = findViewById(R.id.uploadImageButton);
//        btnListTurf = findViewById(R.id.btnListTurf);
//        imageText = findViewById(R.id.imageText);
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            imageUri = data.getData();
//            imageText.setText("Image Selected");
//            Log.d(TAG, "Image URI: " + imageUri.toString());
//        }
//    }
//
//    private void handleListTurf() {
//        String companyNameText = companyName.getText().toString().trim();
//        String emailText = email.getText().toString().trim();
//        String contactText = contact.getText().toString().trim();
//        String addressText = address.getText().toString().trim();
//        String sportText = sport.getText().toString().trim();
//        String priceText = price.getText().toString().trim();
//
//        if (companyNameText.isEmpty() || emailText.isEmpty() || contactText.isEmpty() ||
//                addressText.isEmpty() || sportText.isEmpty() || priceText.isEmpty()) {
//            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (imageUri != null) {
//            uploadImageToStorage(companyNameText, emailText, contactText, addressText, sportText, priceText);
//        } else {
//            saveTurfData(companyNameText, emailText, contactText, addressText, sportText, priceText, null);
//        }
//    }
//
//    private void uploadImageToStorage(String companyName, String email, String contact, String address, String sport, String price) {
//        // Generate a unique identifier for each image
//        StorageReference fileRef = storageReference.child(UUID.randomUUID().toString());
//
//        fileRef.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                    String imageUrl = uri.toString();
//                    Log.d(TAG, "Image uploaded successfully, URL: " + imageUrl);
//                    saveTurfData(companyName, email, contact, address, sport, price, imageUrl);
//                }))
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "Image upload failed: " + e.getMessage());
//                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
////    private void saveTurfData(String companyName, String email, String contact, String address, String sport, String price, @Nullable String imageUrl) {
////        // Prepare turf data to be saved in Firestore
////        Map<String, Object> turf = new HashMap<>();
////        turf.put("companyName", companyName);
////        turf.put("email", email);
////        turf.put("contact", contact);
////        turf.put("address", address);
////        turf.put("sport", sport);
////        turf.put("price", price);
////
////        // Only add the image URL if it exists
////        if (imageUrl != null) {
////            turf.put("image", imageUrl);
////        }
////
////        // Save data to Firestore under the "listturfs" collection
////        db.collection("listturfs")
////                .add(turf)
////                .addOnSuccessListener(documentReference -> {
////                    Toast.makeText(ListTurf.this, "Turf Listed Successfully!", Toast.LENGTH_SHORT).show();
////                    Log.d(TAG, "Turf data saved with ID: " + documentReference.getId());
////                    clearFields();
////                })
////                .addOnFailureListener(e -> {
////                    Log.e(TAG, "Failed to list turf: " + e.getMessage());
////                    Toast.makeText(ListTurf.this, "Failed to list turf: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////                });
////    }
//
//private void saveTurfData(String companyName, String email, String contact, String address, String sport, String price, @Nullable String imageUrl) {
//    // Prepare turf data to be saved in Firestore
//    Map<String, Object> turf = new HashMap<>();
//    turf.put("companyName", companyName);
//    turf.put("email", email);
//    turf.put("contact", contact);
//    turf.put("address", address);
//    turf.put("sport", sport);
//    turf.put("price", price);
//
//    // Only add the image URL if it exists
//    if (imageUrl != null) {
//        turf.put("image", imageUrl);
//    }
//
//    // Save data to Firestore under the "listturfs" collection
//    db.collection("listturfs")
//            .add(turf)
//            .addOnSuccessListener(documentReference -> {
//                // Save the document ID as turfId
//                String turfId = documentReference.getId();
//                db.collection("listturfs").document(turfId).update("turfId", turfId) // update document with turfId
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(ListTurf.this, "Turf Listed Successfully with ID!", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "Turf data saved with ID: " + turfId);
//                            clearFields();
//                        })
//                        .addOnFailureListener(e -> {
//                            Log.e(TAG, "Failed to update turf ID: " + e.getMessage());
//                            Toast.makeText(ListTurf.this, "Failed to update turf ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//            })
//            .addOnFailureListener(e -> {
//                Log.e(TAG, "Failed to list turf: " + e.getMessage());
//                Toast.makeText(ListTurf.this, "Failed to list turf: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//}
//
//
//    private void clearFields() {
//        companyName.setText("");
//        email.setText("");
//        contact.setText("");
//        address.setText("");
//        sport.setText("");
//        price.setText("");
//        imageText.setText("Image");
//        imageUri = null;
//    }
//}

package com.example.turfmobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ListTurf extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "ListTurf";

    private EditText companyName, email, contact, address, sport, price;
    private Button uploadImageButton, btnListTurf;
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
    }

    private void initializeUI() {
        companyName = findViewById(R.id.companyName);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        sport = findViewById(R.id.sport);
        price = findViewById(R.id.price);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        btnListTurf = findViewById(R.id.btnListTurf);
        imageText = findViewById(R.id.imageText);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
        // Generate a unique identifier for each image
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
                    String turfId = documentReference.getId();
                    db.collection("listturfs").document(turfId).update("turfId", turfId)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ListTurf.this, "Turf Listed Successfully with ID!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Turf data saved with ID: " + turfId);
                                clearFields();
                                openScheduleActivity(turfId);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update turf ID: " + e.getMessage());
                                Toast.makeText(ListTurf.this, "Failed to update turf ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
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

    private void openScheduleActivity(String turfId) {
        Intent intent = new Intent(ListTurf.this, ListTurf.class);
        intent.putExtra("turfId", turfId);
        startActivity(intent);
    }
}
