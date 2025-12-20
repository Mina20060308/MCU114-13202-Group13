package com.example.notes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.notes.database.Task;
import com.example.notes.database.TaskRepository;

import java.util.Calendar;

public class AddTaskFragment extends Fragment {

    private EditText editTitle;
    private EditText editTime1; // 對應 XML 的 etTime1
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        editTitle = view.findViewById(R.id.editTaskTitle);
        editTime1 = view.findViewById(R.id.etTime1);
        btnSave = view.findViewById(R.id.btnSaveTask);

        TaskRepository repository = new TaskRepository(requireContext());

        // 點時間欄位 → 跳出日期選擇器
        editTime1.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view1, y, m, d) -> {
                        // 月份 +1 因為 Calendar 的月從 0 開始
                        String date = y + "-" + (m + 1) + "-" + d;
                        editTime1.setText(date);
                    },
                    year,
                    month,
                    day
            );

            dialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String date = editTime1.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(getContext(), "請輸入任務內容", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(date)) {
                Toast.makeText(getContext(), "請選擇日期", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task(
                    0,
                    title,
                    date,
                    null,
                    "早上",
                    false
            );

            repository.insertTask(task);

            Toast.makeText(getContext(), "已新增任務", Toast.LENGTH_SHORT).show();
            editTitle.setText("");
            editTime1.setText("");
        });

        return view;
    }
}

