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

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_all_tasks, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerAllTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        TaskRepository repository = new TaskRepository(requireContext());
        List<Task> taskList = repository.getAllTasks();

        TaskAdapter adapter = new TaskAdapter(taskList, task -> {
            // 之後再處理「完成 / 未完成」
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}