package com.uts.onlineforum;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button btnLogin, btnRegister;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        btnLogin.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                firestore.collection("users").document(user)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String storedHash = documentSnapshot.getString("password");
                                String inputHash = hashPassword(pass);

                                if (storedHash != null && storedHash.equals(inputHash)) {
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    getSharedPreferences("UserSession", MODE_PRIVATE)
                                            .edit()
                                            .putString("loggedInUser", user)
                                            .apply();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
