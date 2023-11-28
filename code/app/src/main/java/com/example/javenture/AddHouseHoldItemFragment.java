package com.example.javenture;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.javenture.databinding.FragmentAddHouseholdItemBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddHouseHoldItemFragment extends Fragment {

    private final String TAG = "AddHouseHoldItemFrag";

    private FragmentAddHouseholdItemBinding binding;
    private TextInputEditText makeEditText;
    private TextInputEditText modelEditText;
    private TextInputLayout serialNumberTextInputLayout;
    private TextInputEditText serialNumberEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText valueEditText;
    private TextInputEditText dateEditText;
    private ChipInputView chipInputView;
    private TextInputEditText commentEditText;
    private RecyclerView imageListRecyclerView;
    private ImageAdapter imageAdapter;
    private Button addImageBtn;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<Void> serialNumberScannerLauncher;
    private ArrayList<ImageItem> imageItems;

    private HouseHoldItemViewModel houseHoldItemViewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        cameraLauncher = registerForActivityResult(new CameraActivityResultContract(), imageItem -> {
            if (imageItem != null) {
                Log.d(TAG, "result: " + imageItem.getLocalUri().toString());
                context.grantUriPermission(context.getPackageName(), imageItem.getLocalUri(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageItems.add(imageItem);
                imageAdapter.notifyItemInserted(imageItems.size() - 1);
            } else {
                Log.e(TAG, "no image selected");
            }
        });
        serialNumberScannerLauncher = registerForActivityResult(new CameraActivityResultContract(), imageItem -> {
            if (imageItem == null) {
                Log.e(TAG, "no image selected");
                return;
            }
            if (!imageItem.isLocal()) {
                Log.e(TAG, "remote image not supported");
                return;
            }
            InputImage image;
            try {
                image = InputImage.fromFilePath(context, imageItem.getLocalUri());
            } catch (IOException e) {
                Log.e(TAG, "failed to create InputImage from local uri");
                return;
            }
            new SerialNumberScanner(image).scan(new SerialNumberScanner.OnCompleteListener() {
                @Override
                public void onSuccess(String serialNumber) {
                    if (serialNumber.isEmpty()) {
                        Log.d("TAG", "no serial number found");
                        if (getView() != null) {
                            Snackbar.make(getView(), "No serial number found", Snackbar.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    serialNumberEditText.setText(serialNumber);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "failed to scan serial number");
                }
            });
        });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddHouseholdItemBinding.inflate(inflater, container, false);
        makeEditText = binding.makeEditText;
        modelEditText = binding.modelEditText;
        serialNumberTextInputLayout = binding.serialNumberTextInputLayout;
        serialNumberEditText = binding.serialNumberEditText;
        descriptionEditText = binding.descriptionEditText;
        valueEditText = binding.valueEditText;
        dateEditText = binding.datePurchasedEditText;
        chipInputView = binding.chipInputView;
        chipInputView.setHint("Tags");
        commentEditText = binding.commentEditText;
        imageListRecyclerView = binding.imageListRecyclerView;
        addImageBtn = binding.addImageButton;


        houseHoldItemViewModel = new ViewModelProvider(requireActivity()).get(HouseHoldItemViewModel.class);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.setMenuItemVisibility(R.id.action_sort_and_filter, false);
            mainActivity.setMenuItemVisibility(R.id.action_scan_barcode, false);
        }

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(AddHouseHoldItemFragment.this);

        AddHouseHoldItemFragmentArgs args = AddHouseHoldItemFragmentArgs.fromBundle(getArguments());
        if (args.getItem() != null) {
            updateUI(args.getItem());
        }

        imageItems = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageItems);
        imageListRecyclerView.setAdapter(imageAdapter);
        imageListRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        addImageBtn.setOnClickListener(v -> {
            cameraLauncher.launch(null);
        });

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
        serialNumberTextInputLayout.setEndIconOnClickListener(v -> {
            serialNumberScannerLauncher.launch(null);
        });

        binding.addFab.setOnClickListener(v -> {
            boolean isValid = true;

            String make = makeEditText.getText().toString();
            String model = modelEditText.getText().toString();
            String serialNumber = serialNumberEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String value = valueEditText.getText().toString();
            String date = dateEditText.getText().toString();
            List<String> tags = chipInputView.getChipWords();
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

            HouseHoldItem houseHoldItem = new HouseHoldItem(null, description, make, LocalDate.parse(date, formatter), Double.parseDouble(value), serialNumber, comment, model, imageItems, tags);

            houseHoldItemViewModel.addItem(houseHoldItem);

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
        descriptionEditText.setText(item.getDescription());
        valueEditText.setText(String.format("%.2f", item.getPrice()));
    }
}