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
import com.example.notes.database.TaskDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AllTasksFragment extends Fragment {

    private RecyclerView rvAllTasks;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        rvAllTasks = view.findViewById(R.id.rvAllTasks);
        rvAllTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new TaskDatabaseHelper(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) return view;

        loadAllTasks();

        return view;
    }

    private void loadAllTasks() {
        List<Task> allTasks = dbHelper.getTasksByUser(userId);
        List<Task> pendingTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (!task.isDone()) pendingTasks.add(task);
        }

        adapter = new TaskAdapter(pendingTasks, (task, position) -> {
            dbHelper.updateTaskDone(task.getId(), true);
            loadAllTasks();
        });

        rvAllTasks.setAdapter(adapter);
    }
}
