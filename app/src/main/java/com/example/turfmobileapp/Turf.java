// Turf.java
package com.example.turfmobileapp;

public class Turf {
    private String id;
    private String companyName;
    private String email;
    private String contact;
    private String address;
    private String sport;
    private String price;
    private String image;

    // No-argument constructor for Firestore
    public Turf() { }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) { this.address = address; }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) { this.sport = sport; }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) { this.price = price; }

    public String getImage() {
        return image;
    }

    public void setImage(String image) { this.image = image; }
}
