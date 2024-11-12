package com.example.tuluyanapp.fragments;

import com.google.firebase.firestore.PropertyName;

public class TenantProfileClass {
    private String firstName;
    private String middleName;
    private String lastName;
    private String address;
    private String contactNo;
    private String email;
    private String age;
    private String birthdate;
    private String tenantId; // Added tenantId field

    // Empty constructor (required for Firebase)
    public TenantProfileClass() {}

    // Constructor with parameters including tenantId
    public TenantProfileClass(String firstName, String middleName, String lastName, String address,
                              String contactNo, String email, String age, String birthdate, String tenantId) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.address = address;
        this.contactNo = contactNo;
        this.email = email;
        this.age = age;
        this.birthdate = birthdate;
        this.tenantId = tenantId;
    }

    // Getters and setters with Firestore field annotations
    @PropertyName("First-Name")
    public String getFirstName() { return firstName; }

    @PropertyName("First-Name")
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @PropertyName("Middle-Name")
    public String getMiddleName() { return middleName; }

    @PropertyName("Middle-Name")
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    @PropertyName("Last-Name")
    public String getLastName() { return lastName; }

    @PropertyName("Last-Name")
    public void setLastName(String lastName) { this.lastName = lastName; }

    @PropertyName("Address")
    public String getAddress() { return address; }

    @PropertyName("Address")
    public void setAddress(String address) { this.address = address; }

    @PropertyName("Contact-No")
    public String getContactNo() { return contactNo; }

    @PropertyName("Contact-No")
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    @PropertyName("userAccount")
    public String getEmail() { return email; }

    @PropertyName("userAccount")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("Age")
    public String getAge() { return age; }

    @PropertyName("Age")
    public void setAge(String age) { this.age = age; }

    @PropertyName("Birthdate")
    public String getBirthdate() { return birthdate; }

    @PropertyName("Birthdate")
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getTenantId() { return tenantId; }

    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
