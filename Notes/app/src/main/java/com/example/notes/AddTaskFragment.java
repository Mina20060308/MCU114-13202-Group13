package com.example.notes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.notes.database.Task;
import com.example.notes.database.TaskRepository;

import java.util.Calendar;

public class AddTaskFragment extends Fragment {

    private EditText editTitle, etDateTime;
    private CheckBox cbDone;
    private Button btnSave;
    private int userId;

    private int year, month, day, hour, minute;
    private boolean isDateTimeSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        editTitle = view.findViewById(R.id.editTaskTitle);
        cbDone = view.findViewById(R.id.cbDone);
        etDateTime = view.findViewById(R.id.etTime1);
        btnSave = view.findViewById(R.id.btnSaveTask);

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) {
            Toast.makeText(getContext(), "使用者資訊錯誤", Toast.LENGTH_SHORT).show();
            return view;
        }

        TaskRepository repository = new TaskRepository(requireContext());

        etDateTime.setOnClickListener(v -> showDateTimePicker());

        btnSave.setOnClickListener(v -> saveTask(repository));

        return view;
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, y, m, d) -> {
                    year = y;
                    month = m;
                    day = d;

                    TimePickerDialog timePicker = new TimePickerDialog(
                            requireContext(),
                            (view1, h, min) -> {
                                hour = h;
                                minute = min;
                                isDateTimeSelected = true;
                                etDateTime.setText(String.format("%04d-%02d-%02d %02d:%02d",
                                        year, month + 1, day, hour, minute));
                            },
                            hour,
                            minute,
                            true
                    );
                    timePicker.show();
                },
                year,
                month,
                day
        );
        datePicker.show();
    }

    private void saveTask(TaskRepository repository) {
        String title = editTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "請輸入任務內容", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateText = "";
        String timeText = "";

        if (isDateTimeSelected && etDateTime.getText() != null) {
            String[] parts = etDateTime.getText().toString().split(" ");
            if (parts.length == 2) {
                dateText = parts[0];
                timeText = parts[1];
            } else if (parts.length == 1) {
                timeText = parts[0];
            }
        }

        Task task = new Task(
                0,
                title,
                dateText,
                timeText,
                "早上",
                cbDone.isChecked(),
                userId
        );

        new Thread(() -> {
            repository.insertTask(task);

            if (!task.isDone() && !TextUtils.isEmpty(task.getTime())) {
                scheduleNotification(task);
            }

            Context context = getContext();
            if (context != null && isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(context, "已新增任務", Toast.LENGTH_SHORT).show();
                    editTitle.setText("");
                    cbDone.setChecked(false);
                    etDateTime.setText("");
                    etDateTime.setHint("選擇日期時間");
                    isDateTimeSelected = false;
                });
            }
        }).start();
    }

    private void scheduleNotification(Task task) {
        if (task.isDone() || TextUtils.isEmpty(task.getTime())) return;

        Context context = getContext();
        if (context == null) return;

        AlarmManager am = context.getSystemService(AlarmManager.class);
        if (am == null) return;

        String[] hm = task.getTime().split(":");
        int hour = Integer.parseInt(hm[0]);
        int minute = Integer.parseInt(hm[1]);

        Calendar alarmTime = Calendar.getInstance();

        if (TextUtils.isEmpty(task.getDate())) {
            // 每日任務
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.add(Calendar.MINUTE, -10); // ✅ 提前10分鐘
        } else {
            // 指定日期任務
            String[] ymd = task.getDate().split("-");
            alarmTime.set(Calendar.YEAR, Integer.parseInt(ymd[0]));
            alarmTime.set(Calendar.MONTH, Integer.parseInt(ymd[1]) - 1);
            alarmTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ymd[2]));
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.add(Calendar.MINUTE, -10); // ✅ 提前10分鐘
        }


        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("TITLE", task.getTitle());

        int requestCode = task.getId();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setAlarmClock(new AlarmManager.AlarmClockInfo(alarmTime.getTimeInMillis(), pendingIntent), pendingIntent);
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(context, "請在系統設定允許精準鬧鐘", Toast.LENGTH_SHORT).show()
                    );
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
            }
        } catch (SecurityException e) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(context, "無法排程精準鬧鐘，請在系統設定允許", Toast.LENGTH_SHORT).show()
            );
        }
    }
}
