package com.example.javenture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javenture.databinding.FragmentHouseholdItemsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HouseHoldItemsFragment extends Fragment {

    private FragmentHouseholdItemsBinding binding;
    private AuthenticationService authService;
    private RecyclerView householdItemList;
    private HouseHoldItemsAdapter houseHoldItemsAdapter;
    private HouseHoldItemViewModel houseHoldItemViewModel;
    private SortAndFilterViewModel sortAndFilterViewModel;
    private static boolean snackbarShownThisSession = false;


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


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(HouseHoldItemsFragment.this);

        // TODO check if sign in works
        authService.signInAnonymously(new AuthenticationService.OnSignInListener() {
            @Override
            public void onSignIn() {
                // display Snackbar only once

                if (!snackbarShownThisSession) {
                    Snackbar.make(binding.getRoot(), "Signed in anonymously", Snackbar.LENGTH_SHORT)
                            .setAnchorView(binding.totalMonthlyChargeContainer)
                            .setAction("Action", null).show();
                    snackbarShownThisSession = true;
                }

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
                Snackbar.make(binding.getRoot(), "Sign in failed", Snackbar.LENGTH_SHORT)
                        .setAnchorView(binding.totalMonthlyChargeContainer)
                        .setAction("Action", null).show();
            }
        });

        binding.addFab.setOnClickListener(v -> {
            navController.navigate(R.id.add_item_action);
        });

        houseHoldItemsAdapter.setOnItemClickListener(new HouseHoldItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HouseHoldItemsFragmentDirections.EditItemAction action = HouseHoldItemsFragmentDirections.editItemAction();
                action.setItem(houseHoldItemViewModel.getHouseHoldItem(position));
                navController.navigate(action);
            }
        });

        houseHoldItemsAdapter.setMultiSelectionModeListener(new HouseHoldItemsAdapter.MultiSelectionModeListener() {
            @Override
            public void onMultiSelectionModeEnter() {
                binding.addFab.setVisibility(View.GONE);
                binding.exitMultiSelectionFab.setVisibility(View.VISIBLE);
                binding.deleteFab.setVisibility(View.VISIBLE);
                binding.tagAssignFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMultiSelectionModeExit() {
                binding.addFab.setVisibility(View.VISIBLE);
                binding.exitMultiSelectionFab.setVisibility(View.GONE);
                binding.deleteFab.setVisibility(View.GONE);
                binding.tagAssignFab.setVisibility(View.GONE);
            }
        });

        binding.exitMultiSelectionFab.setOnClickListener(v -> {
            houseHoldItemsAdapter.exitMultiSelectionMode();
        });

        binding.deleteFab.setOnClickListener(v -> {
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
        binding.tagAssignFab.setOnClickListener(v -> {
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

    private void updateTotalEstimatedValue() {
        binding.totalEstimatedValue.setText(String.format("%.2f", houseHoldItemViewModel.getTotalEstimatedValue()));
    }

}