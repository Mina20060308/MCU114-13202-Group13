package com.example.notes;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Android 13+ 通知權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // 建立通知頻道（Android 8+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "task_channel",
                    "提醒事項通知",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("任務提醒");
            channel.enableVibration(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // 接收 LoginActivity 傳來的 userId
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            finish();
            return;
        }

        Button btnToday = findViewById(R.id.btnToday);
        Button btnCompleted = findViewById(R.id.btnDone);
        Button btnAll = findViewById(R.id.btnAll);
        TextView tvAddTask = findViewById(R.id.tvAdd);

        btnToday.setOnClickListener(v -> openFragment(new TodayFragment()));
        btnCompleted.setOnClickListener(v -> openFragment(new CompletedFragment()));
        btnAll.setOnClickListener(v -> openFragment(new AllTasksFragment()));
        tvAddTask.setOnClickListener(v -> openFragment(new AddTaskFragment()));

        // 使用 OnBackPressedDispatcher 處理返回
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    getSupportFragmentManager().executePendingTransactions();
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        findViewById(R.id.home_menu_layout).setVisibility(View.VISIBLE);
                    }
                } else {
                    finish();
                }
            }
        });
    }

    private void openFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("USER_ID", userId);
        fragment.setArguments(bundle);

        findViewById(R.id.home_menu_layout).setVisibility(View.GONE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
