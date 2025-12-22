package com.example.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

    private EditText editTitle, etTime;
    private CheckBox cbDone;
    private Button btnSave;
    private int userId;

    private int year, month, day, hour, minute;
    private boolean isDateTimeSelected = false;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        editTitle = view.findViewById(R.id.editTaskTitle);
        cbDone = view.findViewById(R.id.cbDone);
        etTime = view.findViewById(R.id.etTime1);
        btnSave = view.findViewById(R.id.btnSaveTask);

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) {
            Toast.makeText(getContext(), "使用者資訊錯誤", Toast.LENGTH_SHORT).show();
            return view;
        }

        TaskRepository repository = new TaskRepository(requireContext());
        etTime.setHint("選擇日期時間");

        etTime.setOnClickListener(v -> showDateTimePicker());

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
                                etTime.setText(String.format(
                                        "%04d-%02d-%02d %02d:%02d",
                                        year, month + 1, day, hour, minute
                                ));
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

        long triggerTime;
        if (isDateTimeSelected) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, hour, minute, 0);
            triggerTime = c.getTimeInMillis();

            if (triggerTime <= System.currentTimeMillis()) {
                Toast.makeText(
                        getContext(),
                        "選擇的時間已過，請重新選擇",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
        } else {
            triggerTime = System.currentTimeMillis() + 60000; // 預設 1 分鐘後
        }

        Task task = new Task(
                0,
                title,
                isDateTimeSelected ? etTime.getText().toString() : null,
                null,
                "早上",
                cbDone.isChecked(),
                userId
        );

        new Thread(() -> {
            repository.insertTask(task);
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {

                    AlarmHelper.scheduleAlarm(
                            requireContext().getApplicationContext(),
                            triggerTime,
                            title
                    );

                    Toast.makeText(
                            getContext(),
                            "已新增任務，將在指定時間通知",
                            Toast.LENGTH_SHORT
                    ).show();

                    editTitle.setText("");
                    cbDone.setChecked(false);
                    etTime.setText("");
                    etTime.setHint("選擇日期時間");
                    isDateTimeSelected = false;
                });
            }
        }).start();
    }
}
