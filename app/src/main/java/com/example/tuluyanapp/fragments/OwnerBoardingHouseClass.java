package com.example.tuluyanapp.fragments;

public class OwnerBoardingHouseClass {
    private String title;
    private double price;
    private String paymentOption;
    private int slots;
    private String address;
    private String description;
    private String otherDetails;
    private String boardingHouseId; // New field for boarding house ID

    // Constructor
    public OwnerBoardingHouseClass(String title, double price, String paymentOption, int slots, String address, String description, String otherDetails, String boardingHouseId) {
        this.title = title;
        this.price = price;
        this.paymentOption = paymentOption;
        this.slots = slots;
        this.address = address;
        this.description = description;
        this.otherDetails = otherDetails;
        this.boardingHouseId = boardingHouseId;
    }

    // Empty Constructor
    public OwnerBoardingHouseClass() {}

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public void setPaymentOption(String paymentOption) {
        this.paymentOption = paymentOption;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }

    public String getBoardingHouseId() {
        return boardingHouseId;
    }

    public void setBoardingHouseId(String boardingHouseId) {
        this.boardingHouseId = boardingHouseId;
    }

    // toString() for debugging and logging
    @Override
    public String toString() {
        return "OwnerBoardingHouseClass{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", paymentOption='" + paymentOption + '\'' +
                ", slots=" + slots +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", otherDetails='" + otherDetails + '\'' +
                ", boardingHouseId='" + boardingHouseId + '\'' +
                '}';
    }
}
