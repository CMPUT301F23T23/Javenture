package com.example.javenture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SortAndFilterBottomSheet extends BottomSheetDialogFragment {

    private Button resetButton;
    private Button applyButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sort_and_filter_bottom_sheet, container, false);

        resetButton = view.findViewById(R.id.reset_button);
        applyButton = view.findViewById(R.id.apply_button);

        resetButton.setOnClickListener(v -> {
            // TODO reset filters
        });

        return view;
    }

}
