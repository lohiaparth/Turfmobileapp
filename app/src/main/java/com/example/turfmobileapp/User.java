package com.example.turfmobileapp;

public class User {
    private String uid;       // Unique identifier for the user
    private String name;      // User's name
    private String email;     // User's email
    private String contact;   // User's contact number
    private String userType;  // Type of user (e.g., Player, Owner)

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Parameterized constructor
    public User(String uid, String name, String email, String contact, String userType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.userType = userType;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}