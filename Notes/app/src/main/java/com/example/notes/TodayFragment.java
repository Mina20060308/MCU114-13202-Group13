package com.example.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.database.Task;
import com.example.notes.database.TaskRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private TaskAdapter adapter;
    private TaskRepository repository;
    private RecyclerView recyclerView;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today, container, false);
        repository = new TaskRepository(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) return view;

        View layoutAddTask = view.findViewById(R.id.layoutAddTask);
        EditText etNewTask = view.findViewById(R.id.etNewTask);
        EditText etTime = view.findViewById(R.id.etTime);
        Button btnAddTask = view.findViewById(R.id.btnAddTask);
        CheckBox cbNewTask = view.findViewById(R.id.cbNewTask);

        View tvAddTask = view.findViewById(R.id.tvAddTask);
        tvAddTask.setOnClickListener(v -> {
            layoutAddTask.setVisibility(layoutAddTask.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        // ------------- 時間選擇 -------------
        final int[] year = new int[1], month = new int[1], day = new int[1], hour = new int[1], minute = new int[1];
        final boolean[] isDateTimeSelected = {false};

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year[0] = calendar.get(Calendar.YEAR);
            month[0] = calendar.get(Calendar.MONTH);
            day[0] = calendar.get(Calendar.DAY_OF_MONTH);
            hour[0] = calendar.get(Calendar.HOUR_OF_DAY);
            minute[0] = calendar.get(Calendar.MINUTE);

            new DatePickerDialog(requireContext(), (view1, y, m, d) -> {
                year[0] = y;
                month[0] = m;
                day[0] = d;

                new TimePickerDialog(requireContext(), (view12, h, min) -> {
                    hour[0] = h;
                    minute[0] = min;
                    isDateTimeSelected[0] = true;
                    etTime.setText(String.format("%04d-%02d-%02d %02d:%02d",
                            year[0], month[0] + 1, day[0], hour[0], minute[0]));
                }, hour[0], minute[0], true).show();

            }, year[0], month[0], day[0]).show();
        });
        // ----------------------------------

        btnAddTask.setOnClickListener(v -> {
            String text = etNewTask.getText().toString().trim();
            if (text.isEmpty()) return;

            long triggerTime;
            if (isDateTimeSelected[0]) {
                Calendar c = Calendar.getInstance();
                c.set(year[0], month[0], day[0], hour[0], minute[0], 0);
                triggerTime = c.getTimeInMillis();

                if (triggerTime <= System.currentTimeMillis()) {
                    etTime.setError("時間已過，請重新選擇");
                    return;
                }
            } else {
                triggerTime = System.currentTimeMillis() + 10 * 1000; // 預設 10 秒後
            }

            Task task = new Task(
                    0,
                    text,
                    isDateTimeSelected[0] ? etTime.getText().toString() : null,
                    null,
                    "morning",
                    false,
                    userId
            );

            repository.insertTask(task);

            int alarmId = task.getTitle().hashCode();
            AlarmHelper.scheduleAlarm(requireContext(), triggerTime, task.getTitle());

            loadTodayTasks();

            etNewTask.setText("");
            etTime.setText("");
            etTime.setHint("選擇提醒時間");
            isDateTimeSelected[0] = false;
            cbNewTask.setChecked(false);
            layoutAddTask.setVisibility(View.GONE);
        });

        recyclerView = view.findViewById(R.id.recyclerTodayTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadTodayTasks();
        return view;
    }

    private void loadTodayTasks() {
        List<Task> allTasks = repository.getTasksByUser(userId);
        List<Task> taskList = new ArrayList<>();

        for (Task task : allTasks) {
            taskList.add(task);
        }

        adapter = new TaskAdapter(taskList, task -> {
            repository.updateTaskDone(task.getId(), !task.isDone());
            loadTodayTasks();
        });

        recyclerView.setAdapter(adapter);
    }
}
