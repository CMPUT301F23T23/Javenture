package com.example.javenture;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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

    private SortAndFilterViewModel sortAndFilterViewModel;
    private SortAndFilterOption sortAndFilterOption;

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
        sortAndFilterOption = sortAndFilterViewModel.getSortAndFilterOption().getValue();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();

        filterByDescriptionKeywordsChip.setOnClickListener(v -> {
            if (filterByDescriptionKeywordsChip.isChecked()) {
                showDescriptionKeywordsDialog();
            } else {
                sortAndFilterOption.setDescriptionKeywords(null);
            }
        });

        filterByDateRangeChip.setOnClickListener(v -> {
            if (filterByDateRangeChip.isChecked()) {
                showDateRangeDialog();
            } else {
                sortAndFilterOption.setDateRange(null);
            }
        });



        resetButton.setOnClickListener(v -> {
            sortAndFilterOption.setSortType(null);
            sortAndFilterOption.setSortOption(null);
            sortAndFilterOption.setFilterType(null);
            sortByChipGroup.clearCheck();
            sortOptionChipGroup.clearCheck();
            filterByChipGroup.clearCheck();
        });

        applyButton.setOnClickListener(v -> {
            int checkedChipId = sortByChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_sort_date).getId()) {
                sortAndFilterOption.setSortType("date");
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_description).getId()) {
                sortAndFilterOption.setSortType("description");
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_make).getId()) {
                sortAndFilterOption.setSortType("make");
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_value).getId()) {
                sortAndFilterOption.setSortType("value");
            } else if (checkedChipId == view.findViewById(R.id.chip_sort_tags).getId()) {
                sortAndFilterOption.setSortType("tags");
            } else {
                sortAndFilterOption.setSortType(null);
            }


            checkedChipId = sortOptionChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_ascending).getId()) {
                sortAndFilterOption.setSortOption("ascending");
            } else if (checkedChipId == view.findViewById(R.id.chip_descending).getId()) {
                sortAndFilterOption.setSortOption("descending");
            } else {
                sortAndFilterOption.setSortOption(null);
            }

            checkedChipId = filterByChipGroup.getCheckedChipId();
            if (checkedChipId == view.findViewById(R.id.chip_filter_date_range).getId()) {
                sortAndFilterOption.setFilterType("date_range");
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_description_keywords).getId()) {
                sortAndFilterOption.setFilterType("description_keywords");
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_make).getId()) {
                sortAndFilterOption.setFilterType("make");
            } else if (checkedChipId == view.findViewById(R.id.chip_filter_tags).getId()) {
                sortAndFilterOption.setFilterType("tags");
            } else {
                sortAndFilterOption.setFilterType(null);
            }

            if (sortAndFilterOption.getSortType() == null && sortAndFilterOption.getSortOption() != null) {
                Snackbar.make(view, "Please select a sort type", Snackbar.LENGTH_SHORT).show();
                return;
            } else if (sortAndFilterOption.getSortType() != null && sortAndFilterOption.getSortOption() == null) {
                Snackbar.make(view, "Please select a sort option", Snackbar.LENGTH_SHORT).show();
                return;
            }

            sortAndFilterViewModel.setSortAndFilterOption(sortAndFilterOption);

            dismiss();
        });
    }

    /**
     * Update the UI based on the selected sort type, sort option, and filter type
     */
    private void updateUI() {
        String sortOption = sortAndFilterOption.getSortOption();
        String sortType = sortAndFilterOption.getSortType();
        String filterType = sortAndFilterOption.getFilterType();

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
     * Show a dialog to get the keywords for filtering by description
     */
    private void showDescriptionKeywordsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_chips, null);
        ChipInputView chipInputView = dialogView.findViewById(R.id.chip_input_view);
        chipInputView.setHint("Keywords");
        builder.setView(dialogView);
        builder.setTitle("Filter by description keywords");

        builder.setPositiveButton("Add", (dialog, which) -> {
            sortAndFilterOption.setDescriptionKeywords(chipInputView.getChipWords());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            sortAndFilterOption.setDescriptionKeywords(null);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDateRangeDialog() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Filter by date range");
        final MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        picker.addOnPositiveButtonClickListener(selection -> {
            // Here selection.first is the start date and selection.second is the end date
            LocalDate startDate = Instant.ofEpochMilli(selection.first).atZone(ZoneId.of("UTC")).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(selection.second).atZone(ZoneId.of("UTC")).toLocalDate();
            Pair<LocalDate, LocalDate> dateRange = new Pair<>(startDate, endDate);

            sortAndFilterOption.setDateRange(dateRange);
        });

        picker.addOnNegativeButtonClickListener(dialog -> {
            sortAndFilterOption.setDateRange(null);
        });

        picker.addOnCancelListener(dialog -> {
            sortAndFilterOption.setDateRange(null);
        });

        picker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
    }

}
