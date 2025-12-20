package com.example.notes;

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
import java.util.List;

public class TodayFragment extends Fragment {

    private TaskAdapter adapter;
    private TaskRepository repository;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_today, container, false);

        // Repository
        repository = new TaskRepository(requireContext());

        // 新增輸入區
        View layoutAddTask = view.findViewById(R.id.layoutAddTask);
        EditText etNewTask = view.findViewById(R.id.etNewTask);
        Button btnAddTask = view.findViewById(R.id.btnAddTask);
        CheckBox cbNewTask = view.findViewById(R.id.cbNewTask);

        // 底部「＋ 新增提醒事項」
        View tvAddTask = view.findViewById(R.id.tvAddTask);
        tvAddTask.setOnClickListener(v -> {
            if (layoutAddTask.getVisibility() == View.VISIBLE) {
                layoutAddTask.setVisibility(View.GONE);
            } else {
                layoutAddTask.setVisibility(View.VISIBLE);
            }
        });

        btnAddTask.setOnClickListener(v -> {
            String text = etNewTask.getText().toString().trim();
            if (text.isEmpty()) return;

            boolean isDone = cbNewTask.isChecked();

            Task task = new Task(
                    0,
                    text,
                    "2025-12-20",   // 之後可以換成今天
                    null,
                    "morning",     // 先預設
                    isDone
            );

            repository.insertTask(task);
            loadTodayTasks();

            // 清空狀態
            etNewTask.setText("");
            cbNewTask.setChecked(false);
            layoutAddTask.setVisibility(View.GONE);
        });

        // RecyclerView
        recyclerView = view.findViewById(R.id.recyclerTodayTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初次載入
        loadTodayTasks();

        return view;
    }


    // =========================
    // 只留一個載入方法
    // =========================
    private void loadTodayTasks() {

        List<Task> allTasks = repository.getAllTasks();

        List<Task> morningTasks = new ArrayList<>();
        List<Task> afternoonTasks = new ArrayList<>();
        List<Task> nightTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (task.getPeriod() == null) continue;

            switch (task.getPeriod()) {
                case "morning":
                    morningTasks.add(task);
                    break;
                case "afternoon":
                    afternoonTasks.add(task);
                    break;
                case "night":
                    nightTasks.add(task);
                    break;
            }
        }

        List<Task> taskList = new ArrayList<>();
        taskList.addAll(morningTasks);
        taskList.addAll(afternoonTasks);
        taskList.addAll(nightTasks);

        adapter = new TaskAdapter(taskList, task -> {
            repository.updateTaskDone(task.getId(), !task.isDone());
            loadTodayTasks();
        });

        recyclerView.setAdapter(adapter);
    }
}
