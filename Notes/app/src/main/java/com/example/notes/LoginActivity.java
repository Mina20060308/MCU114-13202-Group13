package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.database.UserRepository;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        userRepo = new UserRepository(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "請輸入帳號與密碼", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userRepo.loginUser(email, password)) {
                Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoRegister.setOnClickListener(v -> {
            // 隱藏登入表單
            findViewById(R.id.login_form).setVisibility(android.view.View.GONE);

            // 載入註冊 Fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
