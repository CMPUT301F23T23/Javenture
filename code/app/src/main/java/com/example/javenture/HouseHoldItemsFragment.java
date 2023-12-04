package com.example.javenture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javenture.databinding.FragmentHouseholdItemsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a Fragment that displays the list of HouseHoldItem objects in a RecyclerView.
 */
public class HouseHoldItemsFragment extends Fragment {

    private FragmentHouseholdItemsBinding binding;
    private AuthenticationService authService;
    private RecyclerView householdItemList;
    private HouseHoldItemsAdapter houseHoldItemsAdapter;
    private HouseHoldItemViewModel houseHoldItemViewModel;
    private SortAndFilterViewModel sortAndFilterViewModel;
    private ActivityResultLauncher<Void> barcodeScannerLauncher;
    private BarcodeRepository barcodeRepository;
    private NavController navController;
    private static boolean snackbarShownThisSession = false;
    private static String TAG = "HouseHoldItemsFragment";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        barcodeScannerLauncher = registerForActivityResult(new CameraActivityResultContract(), imageItem -> {
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
            new BarcodeScanner(image).scan(new BarcodeScanner.OnCompleteListener() {
                @Override
                public void onSuccess(String barcodeValue) {
                    if (barcodeValue.isEmpty()) {
                        Snackbar.make(binding.getRoot(), "No barcode found", Snackbar.LENGTH_LONG)
                                .setAnchorView(binding.totalMonthlyChargeContainer)
                                .setAction("Action", null).show();
                        Log.d(TAG, "no barcode found");
                        return;
                    }
                    Log.d(TAG, "barcode value: " + barcodeValue);
                    barcodeRepository.getHouseHoldItem(barcodeValue, new BarcodeRepository.OnCompleteListener() {
                        @Override
                        public void onSuccess(HouseHoldItem item) {
                            if (item == null) {
                                Snackbar.make(binding.getRoot(), "No item found", Snackbar.LENGTH_LONG)
                                        .setAnchorView(binding.totalMonthlyChargeContainer)
                                        .setAction("Action", null).show();
                                return;
                            }
                            HouseHoldItemsFragmentDirections.AddItemAction action = HouseHoldItemsFragmentDirections.addItemAction(item);
                            navController.navigate(action);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "failed to get item from barcode: ", e);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "failed scan to barcode: ", e);
                }
            });
        });
    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHouseholdItemsBinding.inflate(inflater, container, false);

        authService = new AuthenticationService();

        householdItemList = binding.householdItemList;
        householdItemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        houseHoldItemViewModel = new ViewModelProvider(requireActivity()).get(HouseHoldItemViewModel.class);

        sortAndFilterViewModel = new ViewModelProvider(requireActivity()).get(SortAndFilterViewModel.class);

        houseHoldItemsAdapter = new HouseHoldItemsAdapter(this.getContext(), houseHoldItemViewModel);
        householdItemList.setAdapter(houseHoldItemsAdapter);

        barcodeRepository = new BarcodeRepository();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(HouseHoldItemsFragment.this);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_sort_and_filter) {
                    SortAndFilterBottomSheet sortAndFilterBottomSheet = new SortAndFilterBottomSheet();
                    sortAndFilterBottomSheet.show(getChildFragmentManager(), "sort_and_filter_bottom_sheet");
                    return true;
                } else if (menuItem.getItemId() == R.id.action_scan_barcode) {
                    barcodeScannerLauncher.launch(null);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        authService.signInAnonymously(new AuthenticationService.OnSignInListener() {
            @Override
            public void onSignIn() {
                // display Snackbar only once

                if (!snackbarShownThisSession) {
//                    Snackbar.make(binding.getRoot(), "Signed in anonymously", Snackbar.LENGTH_SHORT)
//                            .setAnchorView(binding.totalMonthlyChargeContainer)
//                            .setAction("Action", null).show();
                    snackbarShownThisSession = true;
                }
                Log.d("HouseHoldItemsFragment", "onViewCreated: signed in anonymously");

                houseHoldItemViewModel.observeItems(sortAndFilterViewModel.getSortAndFilterOption().getValue());

                sortAndFilterViewModel.getSortAndFilterOption().observe(getViewLifecycleOwner(), sortAndFilterOption -> {
                    houseHoldItemViewModel.observeItems(sortAndFilterOption);
                });

                // Observe the LiveData, update the UI when the data changes
                houseHoldItemViewModel.getHouseHoldItems().observe(getViewLifecycleOwner(), houseHoldItems -> {
                    houseHoldItemsAdapter.notifyDataSetChanged();
                    updateTotalEstimatedValue();
                });

            }
            @Override
            public void onSignInFailed() {
                Log.e("auth", "onViewCreated: sign in failed");
                Snackbar.make(binding.getRoot(), "Sign in failed", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.totalMonthlyChargeContainer)
                        .setAction("Action", null).show();
            }
        });

        binding.addFab.setOnClickListener(v -> {
            HouseHoldItemsFragmentDirections.AddItemAction action = HouseHoldItemsFragmentDirections.addItemAction(null);
            navController.navigate(action);
        });

        houseHoldItemsAdapter.setOnItemClickListener(new HouseHoldItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HouseHoldItemsFragmentDirections.EditItemAction action = HouseHoldItemsFragmentDirections.editItemAction(houseHoldItemViewModel.getHouseHoldItem(position));
                navController.navigate(action);
            }
        });

        houseHoldItemsAdapter.setMultiSelectionModeListener(new HouseHoldItemsAdapter.MultiSelectionModeListener() {
            @Override
            public void onMultiSelectionModeEnter() {
                binding.addFab.setVisibility(View.GONE);
                binding.totalEstimatedValueHeader.setVisibility(View.GONE);
                binding.totalEstimatedValue.setVisibility(View.GONE);
                binding.multiDeleteButton.setVisibility(View.VISIBLE);
                binding.multiTagAssignButton.setVisibility(View.VISIBLE);
                binding.exitMultiSelectionModeButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMultiSelectionModeExit() {
                binding.addFab.setVisibility(View.VISIBLE);
                binding.totalEstimatedValueHeader.setVisibility(View.VISIBLE);
                binding.totalEstimatedValue.setVisibility(View.VISIBLE);
                binding.multiDeleteButton.setVisibility(View.GONE);
                binding.multiTagAssignButton.setVisibility(View.GONE);
                binding.exitMultiSelectionModeButton.setVisibility(View.GONE);
            }
        });

        binding.exitMultiSelectionModeButton.setOnClickListener(v -> {
            houseHoldItemsAdapter.exitMultiSelectionMode();
        });

        binding.multiDeleteButton.setOnClickListener(v -> {
            List<HouseHoldItem> selectedItems = houseHoldItemsAdapter.getSelectedItems();
            if (selectedItems.size() == 0) {
                Snackbar.make(binding.getRoot(), "No items selected", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.totalMonthlyChargeContainer)
                        .setAction("Action", null).show();
                return;
            }
            // Create confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Items")
                    .setMessage("Are you sure you want to delete " + selectedItems.size() + " items?")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            houseHoldItemViewModel.deleteItems(selectedItems);
                            houseHoldItemsAdapter.exitMultiSelectionMode();
                        }
                    })
                    .setNegativeButton("Cancel", null)  // Dismiss the dialog if "Cancel" is clicked
                    .show();
        });
        binding.multiTagAssignButton.setOnClickListener(v -> {
            List<HouseHoldItem> selectedItems = houseHoldItemsAdapter.getSelectedItems();
            if (selectedItems.size() == 0) {
                Snackbar.make(binding.getRoot(), "No items selected", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.totalMonthlyChargeContainer)
                        .setAction("Action", null).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_chips, null);
            ChipInputView chipInputView = dialogView.findViewById(R.id.chip_input_view);
            chipInputView.setHint("Tags");
            builder.setView(dialogView);
            builder.setTitle("Assign Tags");

            builder.setPositiveButton("Add", (dialog, which) -> {
                houseHoldItemsAdapter.exitMultiSelectionMode();

                List<String> tags = chipInputView.getChipWords();

                // assign unique tags to items
                for (HouseHoldItem item : selectedItems) {
                    for (String tag : tags) {
                        item.addTag(tag);
                    }
                }
                houseHoldItemViewModel.editItems(selectedItems);
                houseHoldItemsAdapter.exitMultiSelectionMode();
            });
            builder.setNegativeButton("Cancel", null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        houseHoldItemViewModel.stopObserveItems();
        binding = null;
    }

    /**
     * Update the total estimated value view to the current total estimated value
     */
    private void updateTotalEstimatedValue() {
        binding.totalEstimatedValue.setText(String.format("%.2f", houseHoldItemViewModel.getTotalEstimatedValue()));
    }

}