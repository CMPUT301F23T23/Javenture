package com.example.javenture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.javenture.databinding.FragmentEditHouseholdItemBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class EditHouseHoldItemFragment extends Fragment {

    private FragmentEditHouseholdItemBinding binding;
    private TextInputEditText makeEditText;
    private TextInputEditText modelEditText;
    private TextInputEditText serialNumberEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText valueEditText;
    private TextInputEditText dateEditText;
    private ChipInputView chipInputView;
    private TextInputEditText commentEditText;

    private HouseHoldItemViewModel houseHoldItemViewModel;
    private HouseHoldItem selectedItem;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentEditHouseholdItemBinding.inflate(inflater, container, false);
        makeEditText = binding.makeEditText;
        modelEditText = binding.modelEditText;
        serialNumberEditText = binding.serialNumberEditText;
        descriptionEditText = binding.descriptionEditText;
        valueEditText = binding.valueEditText;
        dateEditText = binding.datePurchasedEditText;
        chipInputView = binding.tagInputView;
        chipInputView.setHint("Tags");
        commentEditText = binding.commentEditText;

        houseHoldItemViewModel = new ViewModelProvider(requireActivity()).get(HouseHoldItemViewModel.class);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setMenuItemVisibility(R.id.action_sort_and_filter, false);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(EditHouseHoldItemFragment.this);
        EditHouseHoldItemFragmentArgs args = EditHouseHoldItemFragmentArgs.fromBundle(getArguments());
        selectedItem = args.getItem();
        updateUI(selectedItem);

        // build material date picker
        dateEditText.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select a date");
            MaterialDatePicker<Long> picker = builder.build();

            picker.show(getParentFragmentManager(), picker.toString());

            picker.addOnPositiveButtonClickListener(selection -> {
                Instant instant = Instant.ofEpochMilli(selection);
                LocalDate localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                String formattedDate = localDate.format(formatter);

                dateEditText.setText(formattedDate);
            });
        });

        binding.deleteButton.setOnClickListener(v -> {
            houseHoldItemViewModel.deleteItem(selectedItem);
            navController.navigate(R.id.confirm_action);
        });

        binding.confirmButton.setOnClickListener(v -> {
            boolean isValid = true;

            String make = makeEditText.getText().toString();
            String model = modelEditText.getText().toString();
            String serialNumber = serialNumberEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String value = valueEditText.getText().toString();
            String date = dateEditText.getText().toString();
            ArrayList<String> chipWords = chipInputView.getChipWords();
            ArrayList<Tag> tags = new ArrayList<>();
            for (String word : chipWords) {
                tags.add(new Tag(word));
            }
            String comment = commentEditText.getText().toString();

            if (make.isEmpty()) {
                binding.makeTextInputLayout.setError("Make is required");
                isValid = false;
            } else {
                binding.makeTextInputLayout.setError(null);
            }
            if (model.isEmpty()) {
                binding.modelTextInputLayout.setError("Model is required");
                isValid = false;
            } else {
                binding.modelTextInputLayout.setError(null);
            }
            if (description.isEmpty()) {
                binding.descriptionTextInputLayout.setError("Description is required");
                isValid = false;
            } else {
                binding.descriptionTextInputLayout.setError(null);
            }
            if (value.isEmpty()) {
                binding.valueTextInputLayout.setError("Estimated Value is required");
                isValid = false;
            } else {
                binding.valueTextInputLayout.setError(null);
            }
            if (date.isEmpty()) {
                binding.datePurchasedTextInputLayout.setError("Date of Purchased is required");
                isValid = false;
            } else {
                binding.datePurchasedTextInputLayout.setError(null);
            }

            if (!isValid) {
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            HouseHoldItem houseHoldItem = new HouseHoldItem(selectedItem.getId(), description, make, LocalDate.parse(date, formatter), Double.parseDouble(value), serialNumber, comment, model, null, tags);
            houseHoldItemViewModel.editItem(houseHoldItem);

            navController.navigate(R.id.confirm_action);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setMenuItemVisibility(R.id.action_sort_and_filter, true);
        }
        binding = null;
    }

    /**
     * Update the UI based on the selected item
     * @param item
     */
    private void updateUI(HouseHoldItem item) {

        makeEditText.setText(item.getMake());
        modelEditText.setText(item.getModel());
        serialNumberEditText.setText(item.getSerialNumber());
        descriptionEditText.setText(item.getDescription());
        valueEditText.setText(String.format("%.2f", item.getPrice()));
        dateEditText.setText(item.getFormattedDatePurchased());

        ArrayList<String> tags = new ArrayList<>();
        for (Tag tag : item.getTags()) {
            tags.add(tag.getName());
        }
        chipInputView.addChipsToChipGroup(tags);
    }
}
