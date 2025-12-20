package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.example.notes.R;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1️⃣ 找到元件
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // 2️⃣ 登入按鈕事件
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "請輸入帳號與密碼", Toast.LENGTH_SHORT).show();
                return;
            }

            // 假設驗證成功 → 跳到 HomeActivity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // 登入頁不再返回
        });
        // 3️⃣ 點擊「還沒有帳號？」 → 跳到註冊頁面
        tvGoRegister.setOnClickListener(v -> {
            // 使用 FragmentTransaction
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment()) // fragment_container 是你的 FrameLayout
                    .addToBackStack(null) // 可以按返回鍵回到登入頁
                    .commit();
        });
    }
}

