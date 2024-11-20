package com.example.tuluyanapp.models;

public class ListingBHModels {
    private String boardingHouseId; // Add this field
    private String name;
    private String price;
    private String imageUrl;
    private String ownerName; // Add ownerName field
    private String paymentOption; // Add paymentOption field
    private String distance; // Add distance field

    public ListingBHModels(String boardingHouseId, String name, String price, String imageUrl, String ownerName, String paymentOption, String distance) {
        this.boardingHouseId = boardingHouseId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.ownerName = ownerName;
        this.paymentOption = paymentOption;
        this.distance = distance;
    }

    public String getBoardingHouseId() {
        return boardingHouseId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPaymentOption() {
        return paymentOption;
    }

    public String getDistance() {
        return distance;
    }
}
