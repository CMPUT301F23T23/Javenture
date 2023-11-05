package com.example.javenture;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SortAndFilterBottomSheet extends BottomSheetDialogFragment {

    private Button resetButton;
    private Button applyButton;
    private Chip sortByDateChip;
    private Chip sortByDescriptionChip;
    private Chip sortByMakeChip;
    private Chip sortByValueChip;
    private Chip sortByTagsChip;
    private Chip sortByAscendingChip;
    private Chip sortByDescendingChip;
    private Chip filterByDateRangeChip;
    private Chip filterByDescriptionKeywordsChip;
    private Chip filterByMakeChip;
    private Chip filterByTagsChip;

    private ChipGroup sortByChipGroup;
    private ChipGroup sortOptionChipGroup;
    private ChipGroup filterByChipGroup;

    private String sortType = null;
    private String sortOption = null;
    private String filterType = null;
    private ArrayList<String> keywords = null;

    private SortAndFilterViewModel sortAndFilterViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sort_and_filter_bottom_sheet, container, false);

        resetButton = view.findViewById(R.id.reset_button);
        applyButton = view.findViewById(R.id.apply_button);

        sortByDateChip = view.findViewById(R.id.chip_sort_date);
        sortByDescriptionChip = view.findViewById(R.id.chip_sort_description);
        sortByMakeChip = view.findViewById(R.id.chip_sort_make);
        sortByValueChip = view.findViewById(R.id.chip_sort_value);
        sortByTagsChip = view.findViewById(R.id.chip_sort_tags);
        sortByAscendingChip = view.findViewById(R.id.chip_ascending);
        sortByDescendingChip = view.findViewById(R.id.chip_descending);
        filterByDateRangeChip = view.findViewById(R.id.chip_filter_date_range);
        filterByDescriptionKeywordsChip = view.findViewById(R.id.chip_filter_description_keywords);
        filterByMakeChip = view.findViewById(R.id.chip_filter_make);
        filterByTagsChip = view.findViewById(R.id.chip_filter_tags);

        sortByChipGroup = view.findViewById(R.id.chipGroup_sort);
        sortOptionChipGroup = view.findViewById(R.id.chipGroup_sort_option);
        filterByChipGroup = view.findViewById(R.id.chipGroup_filter);

        sortAndFilterViewModel = new ViewModelProvider(requireActivity()).get(SortAndFilterViewModel.class);

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();

        filterByDescriptionKeywordsChip.setOnClickListener(v -> {
            if (filterByDescriptionKeywordsChip.isChecked()) {
                showChipInputLayoutDialog("Filter by description keywords", "Keywords");
            } else {
                keywords = null;
            }
        });

        resetButton.setOnClickListener(v -> {
            sortType = null;
            sortOption = null;
            filterType = null;
            sortByChipGroup.clearCheck();
            sortOptionChipGroup.clearCheck();
            filterByChipGroup.clearCheck();
        });

        applyButton.setOnClickListener(v -> {
            int checkedChipId = sortByChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_sort_date).getId()) {
                sortType = "date";
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_description).getId()) {
                sortType = "description";
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_make).getId()) {
                sortType = "make";
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_value).getId()) {
                sortType = "value";
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_tags).getId()) {
                sortType = "tags";
            } else {
                sortType = null;
            }


            checkedChipId = sortOptionChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_ascending).getId()) {
                sortOption = "ascending";
            } else if (checkedChipId == view.findViewById(R.id.chip_descending).getId()) {
                sortOption = "descending";
            } else {
                sortOption = null;
            }

            checkedChipId = filterByChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_filter_date_range).getId()) {
                filterType = "date_range";
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_description_keywords).getId()) {
                filterType = "description_keywords";
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_make).getId()) {
                filterType = "make";
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_tags).getId()) {
                filterType = "tags";
            } else {
                filterType = null;
            }

            if (sortType == null && sortOption != null) {
                Snackbar.make(view, "Please select a sort type", Snackbar.LENGTH_SHORT).show();
                return;
            } else if (sortType != null && sortOption == null) {
                Snackbar.make(view, "Please select a sort option", Snackbar.LENGTH_SHORT).show();
                return;
            }

            sortAndFilterViewModel.setSortType(sortType);
            sortAndFilterViewModel.setSortOption(sortOption);
            sortAndFilterViewModel.setFilterType(filterType);
            sortAndFilterViewModel.setKeywords(keywords);
            Log.d("SortAndFilterBottomSheet", "keywords" + keywords);

            dismiss();
        });
    }

    /**
     * Update the UI based on the selected sort type, sort option, and filter type
     */
    private void updateUI() {
        sortOption = sortAndFilterViewModel.getSortOption().getValue();
        sortType = sortAndFilterViewModel.getSortType().getValue();
        filterType = sortAndFilterViewModel.getFilterType().getValue();

        if (sortType != null) {
            if (sortType.equals("date")) {
                sortByDateChip.setChecked(true);
            } else if (sortType.equals("description")) {
                sortByDescriptionChip.setChecked(true);
            } else if (sortType.equals("make")) {
                sortByMakeChip.setChecked(true);
            } else if (sortType.equals("value")) {
                sortByValueChip.setChecked(true);
            } else if (sortType.equals("tag")) {
                sortByTagsChip.setChecked(true);
            }
        }
        if (sortOption != null) {
            if (sortOption.equals("ascending")) {
                sortByAscendingChip.setChecked(true);
            } else if (sortOption.equals("descending")) {
                sortByDescendingChip.setChecked(true);
            }
        }
        if (filterType != null) {
            if (filterType.equals("date_range")) {
                filterByDateRangeChip.setChecked(true);
            } else if (filterType.equals("description_keywords")) {
                filterByDescriptionKeywordsChip.setChecked(true);
            } else if (filterType.equals("make")) {
                filterByMakeChip.setChecked(true);
            } else if (filterType.equals("tag")) {
                filterByTagsChip.setChecked(true);
            }
        }
    }

    /**
     * Show a dialog to get the keywords for filtering by description and by tags
     *
     * @param title title of the dialog
     * @param hint hint for the input field
     */
    private void showChipInputLayoutDialog(String title, String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_chips, null);
        ChipInputView chipInputView = dialogView.findViewById(R.id.chip_input_view);
        chipInputView.setHint(hint);
        builder.setView(dialogView);
        builder.setTitle(title);

        builder.setPositiveButton("Add", (dialog, which) -> {
            keywords = chipInputView.getChipWords();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            keywords = null;
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
