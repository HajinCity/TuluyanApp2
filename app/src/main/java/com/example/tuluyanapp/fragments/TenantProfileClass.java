package com.example.tuluyanapp.fragments;

import com.google.firebase.firestore.Exclude;
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
    private String tenantId;

    @Exclude
    private String password; // Excluded from Firestore deserialization

    // Empty constructor (required for Firebase)
    public TenantProfileClass() {}

    // Constructor
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

    @PropertyName("firstName")
    public String getFirstName() { return firstName; }

    @PropertyName("firstName")
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @PropertyName("middleName")
    public String getMiddleName() { return middleName; }

    @PropertyName("middleName")
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    @PropertyName("lastName")
    public String getLastName() { return lastName; }

    @PropertyName("lastName")
    public void setLastName(String lastName) { this.lastName = lastName; }

    @PropertyName("address")
    public String getAddress() { return address; }

    @PropertyName("address")
    public void setAddress(String address) { this.address = address; }

    @PropertyName("contactNo")
    public String getContactNo() { return contactNo; }

    @PropertyName("contactNo")
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    @PropertyName("email")
    public String getEmail() { return email; }

    @PropertyName("email")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("age")
    public String getAge() { return age; }

    @PropertyName("age")
    public void setAge(String age) { this.age = age; }

    @PropertyName("birthdate")
    public String getBirthdate() { return birthdate; }

    @PropertyName("birthdate")
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getTenantId() { return tenantId; }

    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @Exclude
    public String getPassword() { return password; }

    @Exclude
    public void setPassword(String password) { this.password = password; }
}
