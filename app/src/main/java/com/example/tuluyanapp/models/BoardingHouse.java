package com.example.tuluyanapp.models;

public class BoardingHouse {
    private String id;
    private String name;
    private String imageUrl;
    private String title;

    public BoardingHouse() {}

    public BoardingHouse(String id, String name, String imageUrl, String title) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}
