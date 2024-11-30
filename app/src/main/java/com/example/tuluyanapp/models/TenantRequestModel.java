package com.example.tuluyanapp.models;

public class TenantRequestModel {
    private String id;
    private String firstName;
    private String lastName;
    private String status;
    private String boardingHouseId; // Add field for Boarding House ID
    private String landlordId;      // Add field for Landlord ID

    // Default constructor for Firestore
    public TenantRequestModel() {
    }

    // Constructor with all fields
    public TenantRequestModel(String id, String firstName, String lastName, String status, String boardingHouseId, String landlordId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.boardingHouseId = boardingHouseId;
        this.landlordId = landlordId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBoardingHouseId() {
        return boardingHouseId;
    }

    public void setBoardingHouseId(String boardingHouseId) {
        this.boardingHouseId = boardingHouseId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }
}
