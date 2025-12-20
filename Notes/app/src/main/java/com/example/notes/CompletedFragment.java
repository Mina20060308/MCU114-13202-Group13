package com.example.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

public class CompletedFragment extends Fragment {

    public CompletedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_completed, container, false);


        ListView listView = view.findViewById(R.id.listCompleted);


        String[] completedItems = {"買早餐", "寫功課", "練習 Fragment"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, completedItems);

        listView.setAdapter(adapter);

        return view;
    }
}
