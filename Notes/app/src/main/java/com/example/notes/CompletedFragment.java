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

public class CompletedFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabaseHelper dbHelper;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        recyclerView = view.findViewById(R.id.recyclerCompletedTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new TaskDatabaseHelper(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) return view;

        loadCompletedTasks();

        return view;
    }

    private void loadCompletedTasks() {
        List<Task> allTasks = dbHelper.getTasksByUser(userId);
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.isDone()) completedTasks.add(task);
        }

        adapter = new TaskAdapter(completedTasks, (task, position) -> {
            dbHelper.updateTaskDone(task.getId(), !task.isDone());
            loadCompletedTasks();
        });

        recyclerView.setAdapter(adapter);
    }
}
