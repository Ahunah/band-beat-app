package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/** Users only registration */
public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirm;
    private Button btnRegister;
    private ImageView btnBack;
    private DBHelper db;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DBHelper(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);

        btnRegister.setOnClickListener(v -> doRegister());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Optional: closes current activity
        });
    }



    private void doRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { toast("Name required"); return; }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { toast("Invalid email"); return; }
        if (pass.length() < 6) { toast("Password must be 6+ chars"); return; }
        if (!pass.equals(confirm)) { toast("Passwords do not match"); return; }

        long id = db.registerUser(name, email, pass);
        if (id > 0) { toast("Registered! Please login as User."); finish(); }
        else { toast("Registration failed (email exists?)"); }
    }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
}