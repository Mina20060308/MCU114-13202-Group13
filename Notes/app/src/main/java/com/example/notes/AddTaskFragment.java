package com.example.notes;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class AddTaskFragment extends Fragment {

    private EditText etTime1, etTime2, etTime3;

    public AddTaskFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        etTime1 = view.findViewById(R.id.etTime1);
        etTime2 = view.findViewById(R.id.etTime2);
        etTime3 = view.findViewById(R.id.etTime3);

        View.OnClickListener listener = v -> showTimePicker((EditText) v);

        etTime1.setOnClickListener(listener);
        etTime2.setOnClickListener(listener);
        etTime3.setOnClickListener(listener);

        return view;
    }

    private void showTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                getContext(),
                (picker, hourOfDay, minuteSelected) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteSelected);
                    targetEditText.setText(selectedTime);
                },
                hour,
                minute,
                true
        );

        dialog.show();
    }
}
