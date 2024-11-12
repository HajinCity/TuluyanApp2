package com.example.tuluyanapp.fragments;

public class OwnerProfileClass {
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String contactNo;
    private String email;
    private int age;
    private String birthDate;
    private String landlordUID; // New field added

    // Default Constructor
    public OwnerProfileClass() {
    }

    // Parameterized Constructor
    public OwnerProfileClass(String firstName, String middleName, String lastName, String address,
                             String contactNo, String email, int age, String birthDate, String landlordUID) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.age = age;
        this.birthDate = birthDate;
        this.landlordUID = landlordUID; // Initialize the new field
    }

    // Getters and Setters for each field
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getLandlordUID() {
        return landlordUID;
    }

    public void setLandlordUID(String landlordUID) {
        this.landlordUID = landlordUID;
    }

    // Optional method to get the full name
    public String getFullName() {
        return firstName + " " + (middleName != null ? middleName + " " : "") + lastName;
    }

    // Optional method to get a summary of the profile
    public String getProfileSummary() {
        return "Name: " + getFullName() + "\n" +
                "Address: " + address + "\n" +
                "Contact No: " + contactNo + "\n" +
                "Email: " + email + "\n" +
                "Age: " + age + "\n" +
                "Birth Date: " + birthDate + "\n" +
                "Landlord UID: " + landlordUID;
    }
}
