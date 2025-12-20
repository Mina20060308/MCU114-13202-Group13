package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 啟動 App 先跳登入頁
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // 不保留 MainActivity
    }
}




