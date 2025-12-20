package com.example.notes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {

    private Button btnToday, btnAll, btnDone;
    private TextView tvAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnToday = findViewById(R.id.btnToday);
        btnAll   = findViewById(R.id.btnAll);
        btnDone  = findViewById(R.id.btnDone);
        tvAdd    = findViewById(R.id.tvAdd);

        btnToday.setOnClickListener(v -> openFragment(new TodayFragment()));
        btnAll.setOnClickListener(v -> openFragment(new AllTasksFragment()));
        btnDone.setOnClickListener(v -> openFragment(new CompletedFragment()));
        tvAdd.setOnClickListener(v -> openFragment(new AddTaskFragment()));
    }

    private void openFragment(Fragment fragment) {
        // 隱藏 Home 主畫面
        findViewById(R.id.home_menu_layout).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null) // 讓返回鍵可以回 Home
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Fragment 回退
            getSupportFragmentManager().popBackStack();

            // 延遲一點確保 Fragment 回退完成再顯示 Home
            getSupportFragmentManager().executePendingTransactions();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                findViewById(R.id.home_menu_layout).setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }
}
