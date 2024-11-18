package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OwnerCreateAccActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextMiddleName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxPrivacy;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_create_acc);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextTextName);
        editTextMiddleName = findViewById(R.id.editTextTextMiddleName);
        editTextLastName = findViewById(R.id.editTextTextLastName);
        editTextEmail = findViewById(R.id.editTextTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextTextConfrim);
        Button buttonCreateAccount = findViewById(R.id.createAccountButton);
        checkBoxPrivacy = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar4);

        buttonCreateAccount.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String middleName = editTextMiddleName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(firstName)) {
                editTextFirstName.setError("First name is required.");
                return;
            }

            if (TextUtils.isEmpty(middleName)) {
                editTextMiddleName.setError("Middle name is required.");
                return;
            }

            if (TextUtils.isEmpty(lastName)) {
                editTextLastName.setError("Last name is required.");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Password is required.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                editTextConfirmPassword.setError("Passwords do not match.");
                return;
            }

            if (!checkBoxPrivacy.isChecked()) {
                Toast.makeText(this, "Please accept the privacy policy.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error hashing password.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            storeOwnerData(firstName, middleName, lastName, email, hashedPassword);
                            Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, OwnerLogin.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void storeOwnerData(String firstName, String middleName, String lastName, String email, String hashedPassword) {
        OwnerCreateAccountClass owner = new OwnerCreateAccountClass();

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();
            owner.setFirstName(firstName);
            owner.setMiddleName(middleName);
            owner.setLastName(lastName);
            owner.setEmail(email);
            owner.setPassword(hashedPassword);
            owner.setLandlordUID(uid);

            Map<String, Object> ownerData = new HashMap<>();
            ownerData.put("FirstName", owner.getFirstName());
            ownerData.put("MiddleName", owner.getMiddleName());
            ownerData.put("LastName", owner.getLastName());
            ownerData.put("UserAccount", owner.getEmail());
            ownerData.put("Password", owner.getPassword());
            ownerData.put("LandlordUID", owner.getLandlordUID());

            db.collection("LandlordCollection")
                    .document(uid)
                    .set(ownerData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Owner data stored successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error storing owner data: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(this, "User authentication error.", Toast.LENGTH_LONG).show();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("OwnerCreateAcc", "Error hashing password", e);
            return null;
        }
    }
}
