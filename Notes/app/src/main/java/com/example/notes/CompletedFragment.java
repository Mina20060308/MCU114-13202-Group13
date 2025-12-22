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
import java.util.stream.Collectors;

public class CompletedFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskRepository repository;
    private int userId;

    public CompletedFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        recyclerView = view.findViewById(R.id.recyclerCompletedTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        repository = new TaskRepository(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", -1);
        }
        if (userId == -1) return view;

        loadCompletedTasks();

        return view;
    }

    private void loadCompletedTasks() {
        List<Task> allTasks = repository.getTasksByUser(userId);

        // 只撈已完成的任務
        List<Task> completedTasks = allTasks.stream()
                .filter(Task::isDone)
                .collect(Collectors.toList());

        adapter = new TaskAdapter(completedTasks, task -> {
            // 可取消完成
            repository.updateTaskDone(task.getId(), !task.isDone());
            loadCompletedTasks();
        });

        recyclerView.setAdapter(adapter);
    }
}
