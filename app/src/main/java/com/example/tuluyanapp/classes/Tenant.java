package com.example.tuluyanapp.classes;
public class Tenant {
    // Attributes
    private int tenantID;
    private String tenantName;
    private String tenantMiddleName;
    private String tenantLastname;
    private String dateOfBirth;
    private String gender;
    private String address;
    private String contactNumber;

    // Constructor
    public Tenant(int tenantID, String tenantName, String tenantMiddleName, String tenantLastname, String dateOfBirth, String gender, String address, String contactNumber) {
        // Initialize attributes
    }

    // Method to view available rooms
    public void viewAvailableRooms() {
        // Logic to check room availability
    }

    // Method to apply for a room
    public void applyForRoom(BoardingHouseRoom room) {
        // Logic to apply for a room
    }
}
