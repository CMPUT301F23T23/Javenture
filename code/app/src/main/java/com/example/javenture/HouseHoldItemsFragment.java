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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javenture.databinding.FragmentHouseholdItemsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.net.URI;

public class HouseHoldItemsFragment extends Fragment {

    private FragmentHouseholdItemsBinding binding;
    private AuthenticationService authService;
    private RecyclerView householdItemList;
    private HouseHoldItemsAdapter houseHoldItemsAdapter;
    private HouseHoldItemViewModel houseHoldItemViewModel;

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

        householdItemList = binding.householdItemList;
        householdItemList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        houseHoldItemViewModel = new ViewModelProvider(requireActivity()).get(HouseHoldItemViewModel.class);
        // observe changes to the household items
        houseHoldItemViewModel.getHouseHoldItems().observe(getViewLifecycleOwner(), houseHoldItems -> {
            houseHoldItemsAdapter.notifyDataSetChanged();
//            updateTotalMonthlyCharge();
        });

        houseHoldItemsAdapter = new HouseHoldItemsAdapter(this.getContext(), houseHoldItemViewModel);
        householdItemList.setAdapter(houseHoldItemsAdapter);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(HouseHoldItemsFragment.this);

        // add one dummy item
        ArrayList<URI> dummyImages = new ArrayList<>();
        ArrayList<Tag> dummyTags = new ArrayList<>();
        HouseHoldItem dummyItem = new HouseHoldItem("Silver Refrigerator", "Samsung", LocalDate.of(2023, 9, 10), 1500, "", "", "ABCD123", dummyImages, dummyTags);
        houseHoldItemViewModel.addHouseHoldItem(dummyItem);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}