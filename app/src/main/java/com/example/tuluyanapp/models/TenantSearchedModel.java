package com.example.tuluyanapp.models;

public class TenantSearchedModel {
    private String ownerName;
    private String title; // Boarding house title
    private int price; // Price
    private String selectionOption; // Selection option
    private String propertyPrice; // Combined price and selection option (computed field)
    private String boardingHouseId; // Unique ID for the boarding house
    private String address; // Address of the boarding house

    // Updated Constructor with Address Field
    public TenantSearchedModel(String ownerName, String title, int price, String selectionOption, String boardingHouseId, String address) {
        this.ownerName = ownerName;
        this.title = title;
        this.price = price;
        this.selectionOption = selectionOption;
        this.propertyPrice = "₱" + price + " (" + selectionOption + ")";
        this.boardingHouseId = boardingHouseId;
        this.address = address;
    }

    // Getters and Setters
    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
        this.propertyPrice = "₱" + price + " (" + selectionOption + ")";
    }

    public String getSelectionOption() {
        return selectionOption;
    }

    public void setSelectionOption(String selectionOption) {
        this.selectionOption = selectionOption;
        this.propertyPrice = "₱" + price + " (" + selectionOption + ")";
    }

    public String getPropertyPrice() {
        return propertyPrice; // Optional getter for combined field
    }

    public String getBoardingHouseId() {
        return boardingHouseId;
    }

    public void setBoardingHouseId(String boardingHouseId) {
        this.boardingHouseId = boardingHouseId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
