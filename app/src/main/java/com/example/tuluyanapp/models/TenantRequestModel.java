package com.example.tuluyanapp.models;

public class TenantRequestModel {
    private String firstName;
    private String lastName;
    private String status;

    // Default constructor for Firestore
    public TenantRequestModel() {
    }

    public TenantRequestModel(String firstName, String lastName, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
