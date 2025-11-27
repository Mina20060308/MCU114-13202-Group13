package com.example.notes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etRegEmail = findViewById(R.id.etRegEmail);
        EditText etRegPassword = findViewById(R.id.etRegPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String email = etRegEmail.getText().toString();
            String pass = etRegPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "請輸入 Email 和密碼", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(RegisterActivity.this, "註冊成功：" + email, Toast.LENGTH_SHORT).show();

            // 回到上一頁（登入頁）
            finish();
        });
    }
}
