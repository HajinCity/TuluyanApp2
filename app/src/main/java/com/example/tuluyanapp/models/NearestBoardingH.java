package com.example.tuluyanapp.models;

public class NearestBoardingH {
    private String boardingHouseId;
    private String name;
    private String price;
    private String distance;
    private String imageUrl;
    private String ownerName;
    private String paymentOption; // Add paymentOption field

    public NearestBoardingH(String boardingHouseId, String name, String price, String distance, String imageUrl, String ownerName, String paymentOption) {
        this.boardingHouseId = boardingHouseId;
        this.name = name;
        this.price = price;
        this.distance = distance;
        this.imageUrl = imageUrl;
        this.ownerName = ownerName;
        this.paymentOption = paymentOption;
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

    public String getDistance() {
        return distance;
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
}
