package com.example.tuluyanapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tuluyanapp.MainActivity4;
import com.example.tuluyanapp.OwnerCreateAccActivity;
import com.example.tuluyanapp.Ownerfgp;
import com.example.tuluyanapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TenantBookmark extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_bookmark);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static class OwnerLogin extends AppCompatActivity {
    
        private EditText editTextEmail, editTextPassword;
        private ProgressBar progressBar;
        private FirebaseAuth mAuth;
        private FirebaseFirestore db;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_owner_login);
    
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                        insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
                return insets;
            });
    
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
    
            editTextEmail = findViewById(R.id.editTextTextEmailAddress);
            editTextPassword = findViewById(R.id.editTextTextPassword);
            progressBar = new ProgressBar(this);
    
            Button loginButton = findViewById(R.id.button3);
    
            loginButton.setOnClickListener(v -> {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
    
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is required.");
                    return;
                }
    
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required.");
                    return;
                }
    
                progressBar.setVisibility(View.VISIBLE);
    
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                fetchOwnerData();
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            });
    
            findViewById(R.id.textView8).setOnClickListener(v ->
                    startActivity(new Intent(this, OwnerCreateAccActivity.class))
            );
    
            findViewById(R.id.textView7).setOnClickListener(v ->
                    startActivity(new Intent(this, Ownerfgp.class))
            );
        }
    
        private void fetchOwnerData() {
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
    
                db.collection("LandlordCollection")
                        .document(userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String name = document.getString("FirstName");
                                    Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
    
                                    Intent intent = new Intent(this, MainActivity4.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "No such landlord exists in the database.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Toast.makeText(this, "Failed to retrieve landlord data: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(this, "User authentication error.", Toast.LENGTH_LONG).show();
            }
        }
    }
}