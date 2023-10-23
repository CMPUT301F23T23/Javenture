package com.example.javenture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.javenture.databinding.FragmentHouseholdItemDetailBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HouseHoldItemDetailFragment extends Fragment {

    private FragmentHouseholdItemDetailBinding binding;
    private TextInputEditText makeEditText;
    private TextInputEditText modelEditText;
    private TextInputEditText serialNumberEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText valueEditText;
    private TextInputEditText dateEditText;

    private HouseHoldItemRepository houseHoldItemRepository;
    private AuthenticationService authService;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHouseholdItemDetailBinding.inflate(inflater, container, false);
        makeEditText = binding.makeEditText;
        modelEditText = binding.modelEditText;
        serialNumberEditText = binding.serialNumberEditText;
        descriptionEditText = binding.descriptionEditText;
        valueEditText = binding.valueEditText;
        dateEditText = binding.datePurchasedEditText;

        authService = new AuthenticationService();
        houseHoldItemRepository = new HouseHoldItemRepository(authService.getCurrentUser());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(HouseHoldItemDetailFragment.this);

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

        binding.addButton.setOnClickListener(v -> {
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

            if (!isValid) {
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            // TODO add item to database
            HouseHoldItem houseHoldItem = new HouseHoldItem(description, make, LocalDate.parse(date, formatter), Double.parseDouble(value), serialNumber, "", model, null, null);
            houseHoldItemRepository.addItem(houseHoldItem);

            navController.navigate(R.id.confirm_action);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}