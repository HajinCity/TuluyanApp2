package com.example.tuluyanapp.models;

public class TenantViewApplicationModel {
    private String apartmentName;
    private String price;
    private String status;

    public TenantViewApplicationModel() {
        // Default constructor required for Firestore
    }

    public TenantViewApplicationModel(String apartmentName, String price, String status) {
        this.apartmentName = apartmentName;
        this.price = price;
        this.status = status;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
