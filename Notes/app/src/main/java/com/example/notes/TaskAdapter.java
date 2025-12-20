package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.database.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskCheckedListener {
        void onChecked(Task task);
    }

    private List<Task> taskList;
    private OnTaskCheckedListener listener;

    public TaskAdapter(List<Task> taskList, OnTaskCheckedListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxTask;
        TextView textTaskTitle;
        TextView textTaskTime;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxTask = itemView.findViewById(R.id.checkboxTask);
            textTaskTitle = itemView.findViewById(R.id.textTaskTitle);
            textTaskTime = itemView.findViewById(R.id.textTaskTime);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.textTaskTitle.setText(task.getTitle());
        holder.textTaskTime.setText(task.getTime() == null ? "" : task.getTime());
        holder.checkboxTask.setChecked(task.isDone());

        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onChecked(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
    public void updateTaskList(List<Task> newTaskList) {
        taskList = newTaskList;
        notifyDataSetChanged();
    }
}
