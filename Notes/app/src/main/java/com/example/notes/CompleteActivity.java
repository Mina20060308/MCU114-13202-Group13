package com.example.notes;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class CompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        ListView listView = findViewById(R.id.listCompleted);

        // 模擬已完成事項（你之後可以從資料庫或 SharedPreferences 拿資料）
        String[] completedItems = {
                "買早餐",
                "寫作業",
                "運動 30 分鐘",
                "回訊息"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, completedItems);

        listView.setAdapter(adapter);
    }
}
