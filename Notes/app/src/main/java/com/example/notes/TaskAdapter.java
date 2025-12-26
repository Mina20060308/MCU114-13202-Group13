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
        void onChecked(Task task, int position);
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
        TextView textTaskDate;
        TextView textTaskTime;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxTask = itemView.findViewById(R.id.cbTaskDone);
            textTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            textTaskDate = itemView.findViewById(R.id.tvTaskDate);
            textTaskTime = itemView.findViewById(R.id.tvTaskTime);
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
        final Task task = taskList.get(position);

        holder.textTaskTitle.setText(task.getTitle());

        if (task.getDate() != null && !task.getDate().isEmpty()) {
            holder.textTaskDate.setText(task.getDate());
            holder.textTaskDate.setVisibility(View.VISIBLE);
        } else {
            holder.textTaskDate.setVisibility(View.GONE);
        }

        if (task.getTime() != null && !task.getTime().isEmpty()) {
            holder.textTaskTime.setText("提醒事項 " + task.getTime());
            holder.textTaskTime.setVisibility(View.VISIBLE);
        } else {
            holder.textTaskTime.setVisibility(View.GONE);
        }

        holder.checkboxTask.setOnCheckedChangeListener(null);
        holder.checkboxTask.setChecked(task.isDone());

        final int pos = position;
        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onChecked(task, pos);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTaskAt(int position, Task task) {
        taskList.set(position, task);
        notifyItemChanged(position);
    }

    public void updateTaskList(List<Task> newTaskList) {
        taskList = newTaskList;
        notifyDataSetChanged();
    }
}
