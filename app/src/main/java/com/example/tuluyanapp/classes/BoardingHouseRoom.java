package com.example.tuluyanapp.classes;

public class BoardingHouseRoom {
    // Attributes
    private int roomID;
    private String roomDetails;
    private double rentAmount;
    private boolean availabilityStatus;

    // Constructor
    public BoardingHouseRoom(int roomID, String roomDetails, double rentAmount, boolean availabilityStatus) {
        // Initialize attributes
    }

    // Method to update availability
    public void updateAvailability(boolean status) {
        this.availabilityStatus = status;
    }
}

