package com.example.bandbeat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnAdmin, btnUser;
    private TextView tvRegister;
    private DBHelper db;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBHelper(this);
        session = new Session(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnUser = findViewById(R.id.btnUser);
        tvRegister = findViewById(R.id.tvRegister);

        btnAdmin.setOnClickListener(v -> doLogin("ADMIN"));
        btnUser.setOnClickListener(v -> doLogin("USER"));
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void doLogin(String role) {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (!isValidEmail(email)) {
            toast("Invalid email");
            return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            toast("Password must be 6+ chars");
            return;
        }

        int userId = db.login(email, pass, role);
        if (userId > 0) {
            session.login(role, userId);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            toast("Login failed. Check credentials/role.");
        }
    }

    private boolean isValidEmail(String s) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}