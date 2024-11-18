package com.example.tuluyanapp.models;

public class NearestBoardingH {
    private String name;
    private String price;
    private String distance;
    private String imageUrl;

    public NearestBoardingH(String name, String price, String distance, String imageUrl) {
        this.name = name;
        this.price = price;
        this.distance = distance;
        this.imageUrl = imageUrl;
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
}
