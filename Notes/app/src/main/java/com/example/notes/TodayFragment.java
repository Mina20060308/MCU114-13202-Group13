package com.example.notes;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.database.Task;
import com.example.notes.database.TaskDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private int userId = 1;

    private LinearLayout contentLayout, addTaskLayout;
    private EditText inputMorning, inputAfternoon, inputNight;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today, container, false);

        dbHelper = new TaskDatabaseHelper(requireContext());

        recyclerView = view.findViewById(R.id.recyclerTodayTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contentLayout = view.findViewById(R.id.contentLayout);
        addTaskLayout = view.findViewById(R.id.addTaskLayout);

        inputMorning = view.findViewById(R.id.inputMorning);
        inputAfternoon = view.findViewById(R.id.inputAfternoon);
        inputNight = view.findViewById(R.id.inputNight);

        // Android 13+ 動態請求通知權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        inputMorning.setOnClickListener(v -> showInlineAddTaskAt(inputMorning));
        inputAfternoon.setOnClickListener(v -> showInlineAddTaskAt(inputAfternoon));
        inputNight.setOnClickListener(v -> showInlineAddTaskAt(inputNight));

        addTaskLayout.setOnClickListener(v -> showInlineAddTaskAtTop());

        loadTasks();
        return view;
    }

    // 在指定 EditText 上方新增區塊
    private void showInlineAddTaskAt(EditText targetEditText) {
        LinearLayout newTaskLayout = createInlineTaskLayout();
        contentLayout.addView(newTaskLayout, contentLayout.indexOfChild(targetEditText));
    }

    // 左下新增 → 插在最上方，只會出現一個
    private void showInlineAddTaskAtTop() {
        if (contentLayout.getChildCount() > 0) {
            View firstChild = contentLayout.getChildAt(0);
            if (firstChild.getTag() != null && firstChild.getTag().equals("inlineTaskTop")) return;
        }

        LinearLayout newTaskLayout = createInlineTaskLayout();
        newTaskLayout.setTag("inlineTaskTop");
        contentLayout.addView(newTaskLayout, 0);
    }

    // 建立 inline 區塊
    private LinearLayout createInlineTaskLayout() {
        LinearLayout newTaskLayout = new LinearLayout(getContext());
        newTaskLayout.setOrientation(LinearLayout.HORIZONTAL);
        newTaskLayout.setPadding(0, 20, 0, 0);

        CheckBox cbDone = new CheckBox(getContext());

        LinearLayout textLayout = new LinearLayout(getContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        EditText etTask = new EditText(getContext());
        etTask.setHint("輸入任務內容...");
        etTask.setBackgroundResource(android.R.drawable.edit_text);
        etTask.setPadding(12, 12, 12, 12);

        EditText etTime = new EditText(getContext());
        etTime.setHint("選擇時間");
        etTime.setFocusable(false);
        etTime.setClickable(true);
        etTime.setBackgroundResource(android.R.drawable.edit_text);
        etTime.setPadding(12, 12, 12, 12);

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(getContext(),
                    (view, hour, minute) -> etTime.setText(String.format("%02d:%02d", hour, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            ).show();
        });

        textLayout.addView(etTask);
        textLayout.addView(etTime);

        Button btnAdd = new Button(getContext());
        btnAdd.setText("新增");
        btnAdd.setBackgroundColor(0xFF007AFF);
        btnAdd.setTextColor(0xFFFFFFFF);

        btnAdd.setOnClickListener(v -> {
            String title = etTask.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            if (title.isEmpty()) return;

            Task task = new Task(0, title, "", time, "", false, userId);

            // 插入資料庫並取得自增 ID
            long newId = dbHelper.insertTask(task);
            task.setId((int) newId);

            // 排程通知
            scheduleNotification(task);

            loadTasks();
            ((ViewGroup) newTaskLayout.getParent()).removeView(newTaskLayout);
        });

        newTaskLayout.addView(cbDone);
        newTaskLayout.addView(textLayout);
        newTaskLayout.addView(btnAdd);

        return newTaskLayout;
    }

    private void loadTasks() {
        List<Task> allTasks = dbHelper.getTasksByUser(userId);
        List<Task> todayTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getDate() == null || task.getDate().isEmpty()) todayTasks.add(task);
        }

        if (adapter == null) {
            adapter = new TaskAdapter(todayTasks, (task, position) -> {
                dbHelper.updateTaskDone(task.getId(), !task.isDone());
                task.setDone(!task.isDone());
                adapter.updateTaskAt(position, task);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateTaskList(todayTasks);
        }
    }

    // ===== 提前10分鐘通知 =====
    private void scheduleNotification(Task task) {
        if (task.isDone() || task.getTime() == null || task.getTime().isEmpty()) return;

        Context context = getContext();
        if (context == null) return;

        AlarmManager am = context.getSystemService(AlarmManager.class);
        if (am == null) return;

        String[] hm = task.getTime().split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);

        Calendar now = Calendar.getInstance();
        Calendar alarmTime = (Calendar) now.clone();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.add(Calendar.MINUTE, -10);

        if (alarmTime.before(now)) alarmTime = now;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("TITLE", task.getTitle());

        int requestCode = task.getId();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) flags |= PendingIntent.FLAG_IMMUTABLE;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime.getTimeInMillis(), pendingIntent), pendingIntent);
                } else {
                    // 沒開精準鬧鐘，跳轉到系統設定頁面
                    Toast.makeText(context, "請允許精準鬧鐘權限", Toast.LENGTH_SHORT).show();
                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }
        } catch (SecurityException e) {
            Toast.makeText(context, "無法排程精準鬧鐘，請在系統設定允許", Toast.LENGTH_SHORT).show();
        }
    }

}
