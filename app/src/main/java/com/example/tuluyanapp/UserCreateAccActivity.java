package com.example.tuluyanapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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


public class UserCreateAccActivity extends AppCompatActivity {

    private EditText editTextName, editTextMiddleName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private CheckBox checkBoxPrivacy;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_acc);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeUI();
    }

    private void initializeUI() {
        // Initialize UI elements
        editTextName = findViewById(R.id.editTextTextName);
        editTextMiddleName = findViewById(R.id.editTextMiddleName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextEmail = findViewById(R.id.editTextTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextTextConfrim);
        Button buttonCreateAccount = findViewById(R.id.createAccountButton);
        checkBoxPrivacy = findViewById(R.id.checkBox);
        progressBar = findViewById(R.id.progressBar4);

        buttonCreateAccount.setOnClickListener(v -> createAccount());

        // Add text watcher for middle name input to ensure single uppercase letter
        editTextMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String input = s.toString().toUpperCase();
                    editTextMiddleName.removeTextChangedListener(this); // Prevent recursion
                    editTextMiddleName.setText(input);
                    editTextMiddleName.setSelection(input.length()); // Move cursor to end
                    editTextMiddleName.addTextChangedListener(this);
                }
            }
        });
    }

    private void createAccount() {
        String name = editTextName.getText().toString().trim();
        String middleName = editTextMiddleName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (!validateInputs(name, middleName, lastName, email, password, confirmPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UserCreateAccActivity.this, "Error hashing password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(UserCreateAccActivity.this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String userId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

                        if (userId != null) {
                            Log.d("UserCreateAcc", "User UID: " + userId);

                            UserCreateAccountClass user = new UserCreateAccountClass(name, email);
                            user.setMiddleName(middleName);
                            user.setLastName(lastName);
                            user.setTenantId(userId);

                            // Pass the hashed password to storeUserData
                            storeUserData(user, hashedPassword);
                        } else {
                            handleError("User authentication failed.");
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage = exception.getMessage();
                            if (errorMessage != null && errorMessage.contains("network error")) {
                                handleError("Network error occurred. Please try again.");
                            } else {
                                handleError("Registration failed: " + errorMessage);
                            }
                        } else {
                            handleError("An unknown error occurred during registration.");
                        }
                    }
                });
    }


    private boolean validateInputs(String name, String middleName, String lastName, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("First name is required.");
            return false;
        }
        if (TextUtils.isEmpty(middleName)) {
            editTextMiddleName.setError("Middle initial is required.");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last name is required.");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match.");
            return false;
        }
        if (!checkBoxPrivacy.isChecked()) {
            Toast.makeText(this, "Please accept the privacy policy.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void storeUserData(UserCreateAccountClass user, String Password) {
        db.collection("TenantCollection")
                .document(user.getTenantId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // After successfully storing user, store hashed password
                    db.collection("TenantCollection")
                            .document(user.getTenantId())
                            .update("Password", Password)
                            .addOnSuccessListener(success -> {
                                Log.d("UserCreateAcc", "Hashed password stored successfully.");
                                Toast.makeText(UserCreateAccActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserCreateAccActivity.this, UserLogin.class));
                                finish();
                            })
                            .addOnFailureListener(e -> handleError("Error storing hashed password: " + e.getMessage()));
                })
                .addOnFailureListener(e -> handleError("Error storing user data: " + e.getMessage()));
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
            Log.e("UserCreateAcc", "Error hashing password", e);
            return null;
        }
    }

    private void handleError(String message) {
        Log.e("UserCreateAcc", message);
        Toast.makeText(UserCreateAccActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
