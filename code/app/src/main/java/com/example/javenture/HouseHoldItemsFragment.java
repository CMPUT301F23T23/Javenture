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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javenture.databinding.FragmentHouseholdItemsBinding;
import com.google.android.material.snackbar.Snackbar;

public class HouseHoldItemsFragment extends Fragment {

    private FragmentHouseholdItemsBinding binding;
    private AuthenticationService authService;
    private RecyclerView householdItemList;
    private HouseHoldItemsAdapter houseHoldItemsAdapter;
    private HouseHoldItemViewModel houseHoldItemViewModel;
    private HouseHoldItemRepository houseHoldItemRepository;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHouseholdItemsBinding.inflate(inflater, container, false);
        authService = new AuthenticationService();
        // TODO check if sign in works
        if (authService.getCurrentUser() == null) {
            authService.signInAnonymously(new AuthenticationService.OnSignInListener() {
                @Override
                public void onSignIn() {
                    Snackbar.make(binding.getRoot(), "Signed in anonymously", Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.totalMonthlyChargeContainer)
                            .setAction("Action", null).show();
                }
                @Override
                public void onSignInFailed() {
                    Snackbar.make(binding.getRoot(), "Sign in failed", Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.totalMonthlyChargeContainer)
                            .setAction("Action", null).show();
                }
            });
        }
        houseHoldItemRepository = new HouseHoldItemRepository(authService.getCurrentUser());

        householdItemList = binding.householdItemList;
        householdItemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        houseHoldItemViewModel = new ViewModelProvider(requireActivity()).get(HouseHoldItemViewModel.class);

        // add all items from db to view model
        houseHoldItemRepository.fetchItems(items -> {
            houseHoldItemViewModel.clear();
            for (HouseHoldItem item : items) {
                houseHoldItemViewModel.addHouseHoldItem(item);
            }
        });

        // observe changes to the household items
        houseHoldItemViewModel.getHouseHoldItems().observe(getViewLifecycleOwner(), houseHoldItems -> {
            houseHoldItemsAdapter.notifyDataSetChanged();
            updateTotalEstimatedValue();
        });

        houseHoldItemsAdapter = new HouseHoldItemsAdapter(this.getContext(), houseHoldItemViewModel);
        householdItemList.setAdapter(houseHoldItemsAdapter);


        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(HouseHoldItemsFragment.this);

        binding.addButton.setOnClickListener(v -> {
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void updateTotalEstimatedValue() {
        binding.totalEstimatedValue.setText(String.format("%.2f", houseHoldItemViewModel.getTotalEstimatedValue()));
    }

}