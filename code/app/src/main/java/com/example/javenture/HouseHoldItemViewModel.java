package com.example.javenture;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class HouseHoldItemViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<HouseHoldItem>> houseHoldItems = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<ArrayList<HouseHoldItem>> getHouseHoldItems() {
        return houseHoldItems;
    }

    /**
     * Get an HouseHoldItem object at a given index
     * @param index index of the HouseHoldItem object
     * @return HouseHoldItem
     */
    public HouseHoldItem getHouseHoldItem(int index) {
        return Objects.requireNonNull(houseHoldItems.getValue()).get(index);
    }

    /**
     * Add an HouseHoldItem object to the list of houseHoldItems
     * @param houseHoldItem HouseHoldItem object to be added
     */
    public void addHouseHoldItem(HouseHoldItem houseHoldItem) {
        ArrayList<HouseHoldItem> currentHouseHoldItems = houseHoldItems.getValue();
        if (currentHouseHoldItems != null) {
            currentHouseHoldItems.add(houseHoldItem);
            houseHoldItems.postValue(currentHouseHoldItems);
        }
    }

    /**
     * Remove an HouseHoldItem object at a given index
     * @param index index of the HouseHoldItem object
     */
    public void removeHouseHoldItem(int index) {
        ArrayList<HouseHoldItem> currentHouseHoldItems = houseHoldItems.getValue();
        if (currentHouseHoldItems != null) {
            currentHouseHoldItems.remove(index);
            houseHoldItems.postValue(currentHouseHoldItems);
        }
    }

}
