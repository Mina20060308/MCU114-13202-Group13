package com.example.notes;

import  android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.database.Task;
import com.example.notes.database.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

public class AllTasksFragment extends Fragment {

    private RecyclerView rvAllTasks;
    private TaskAdapter adapter;
    private TaskRepository taskRepo;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        rvAllTasks = view.findViewById(R.id.rvAllTasks);
        rvAllTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        taskRepo = new TaskRepository(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) return view;

        loadAllTasks();
        return view;
    }

    private void loadAllTasks() {
        List<Task> allTasks = taskRepo.getTasksByUser(userId);

        // ⭐只顯示未完成的任務
        List<Task> pendingTasks = allTasks.stream()
                .filter(task -> !task.isDone())
                .collect(Collectors.toList());

        adapter = new TaskAdapter(pendingTasks, task -> {
            // 打勾 → 標記為完成
            taskRepo.updateTaskDone(task.getId(), true);

            // 重新載入全部事項（已完成任務會消失）
            loadAllTasks();
        });

        rvAllTasks.setAdapter(adapter);
    }
}