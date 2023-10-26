package com.example.javenture;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.javenture.databinding.FragmentEditHouseholdItemBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class EditHouseHoldItemFragment extends Fragment {

    private FragmentEditHouseholdItemBinding binding;
    private TextInputEditText makeEditText;
    private TextInputEditText modelEditText;
    private TextInputEditText serialNumberEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText valueEditText;
    private TextInputEditText dateEditText;
    private TextInputEditText tagEditText;
    private ChipGroup tagChipGroup;
    private Set<String> tagNames;

    private HouseHoldItemRepository houseHoldItemRepository;
    private AuthenticationService authService;
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
        tagChipGroup = binding.tagChipGroup;
        tagEditText = binding.tagEditText;

        tagNames = new HashSet<>();
        authService = new AuthenticationService();
        houseHoldItemRepository = new HouseHoldItemRepository(authService.getCurrentUser());

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

        tagEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String enteredText = tagEditText.getText().toString();

                if (!enteredText.isEmpty() && !tagNames.contains(enteredText)) {
                    Chip chip = new Chip(getContext());
                    chip.setText(enteredText);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(v1 -> {
                        tagChipGroup.removeView(chip);
                        tagNames.remove(enteredText);
                    });
                    tagChipGroup.addView(chip);
                    tagEditText.setText("");
                    tagNames.add(enteredText);
                }
                return true;
            }
            return false;
        });

        binding.deleteButton.setOnClickListener(v -> {
            houseHoldItemRepository.deleteItem(selectedItem);
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

            // get tags and add to list
            int chipCount = tagChipGroup.getChildCount();
            ArrayList<Tag> tags = new ArrayList<>();
            for (int i = 0; i < chipCount; i++) {
                Chip chip = (Chip) tagChipGroup.getChildAt(i);
                tags.add(new Tag(chip.getText().toString()));
            }

            if (!isValid) {
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            HouseHoldItem houseHoldItem = new HouseHoldItem(selectedItem.getId(), description, make, LocalDate.parse(date, formatter), Double.parseDouble(value), serialNumber, "", model, null, tags);
            houseHoldItemRepository.editItem(houseHoldItem);

            navController.navigate(R.id.confirm_action);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    /**
     * Update the UI based on the selected item
     * @param item
     */
    private void updateUI(HouseHoldItem item) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (item == null) {
                mainActivity.getToolbar().setTitle("Add Item");
                return;
            }
            mainActivity.getToolbar().setTitle("Edit Item");
        }

        makeEditText.setText(item.getMake());
        modelEditText.setText(item.getModel());
        serialNumberEditText.setText(item.getSerialNumber());
        descriptionEditText.setText(item.getDescription());
        valueEditText.setText(String.format("%.2f", item.getPrice()));
        dateEditText.setText(item.getFormattedDatePurchased());

        // add tags to chip group
        for (Tag tag : item.getTags()) {
            Chip chip = new Chip(getContext());
            chip.setText(tag.getName());
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v1 -> {
                tagChipGroup.removeView(chip);
                tagNames.remove(tag.getName());
            });
            tagChipGroup.addView(chip);
            tagNames.add(tag.getName());
        }
    }
}
