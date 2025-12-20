package com.example.notes;

import android.os.Bundle;
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

public class AllTasksFragment extends Fragment {

    private RecyclerView rvAllTasks;
    private TaskAdapter adapter;
    private TaskRepository taskRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        rvAllTasks = view.findViewById(R.id.rvAllTasks);

        // 初始化資料庫
        taskRepo = new TaskRepository(requireContext());

        // RecyclerView 設定
        rvAllTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        // 讀取任務資料
        loadAllTasks();

        return view;
    }

    private void loadAllTasks() {
        List<Task> tasks = taskRepo.getAllTasks();
        adapter = new TaskAdapter(tasks, task -> {
            // 點擊任務可以做的事情，例如更新完成狀態
            taskRepo.updateTaskDone(task.getId(), !task.isDone());
            loadAllTasks(); // 更新畫面

        });
        rvAllTasks.setAdapter(adapter);
    }
}
