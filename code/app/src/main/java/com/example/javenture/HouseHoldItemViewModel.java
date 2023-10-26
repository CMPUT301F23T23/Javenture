package com.example.javenture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Objects;

public class HouseHoldItemViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<HouseHoldItem>> houseHoldItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<ArrayList<HouseHoldItem>> getHouseHoldItems() {
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
     * Remove an HouseHoldItem object from the list of houseHoldItems
     * @param item HouseHoldItem object to be removed
     */
    public void removeHouseHoldItem(HouseHoldItem item) {
        ArrayList<HouseHoldItem> currentHouseHoldItems = houseHoldItems.getValue();
        if (currentHouseHoldItems != null) {
            currentHouseHoldItems.remove(item);
            houseHoldItems.postValue(currentHouseHoldItems);
        }
    }

    /**
     * Edit an HouseHoldItem object at a given index
     * @param item HouseHoldItem object with updated values
     */
    public void updateHouseHoldItem(HouseHoldItem item) {
        ArrayList<HouseHoldItem> currentHouseHoldItems = houseHoldItems.getValue();
        if (currentHouseHoldItems != null) {
            for (int index = 0; index < currentHouseHoldItems.size(); index++) {
                if (currentHouseHoldItems.get(index).getId().equals(item.getId())) {
                    currentHouseHoldItems.set(index, item);
                    houseHoldItems.postValue(currentHouseHoldItems);
                    break;
                }
            }
        }
    }

    /**
     * Empty the list.
     */
    public void clear() {
        ArrayList<HouseHoldItem> currentHouseHoldItems = houseHoldItems.getValue();
        if (currentHouseHoldItems != null) {
            currentHouseHoldItems.clear();
            houseHoldItems.postValue(currentHouseHoldItems);
        }
    }

    /**
     * Get the total value of all items in the list
     * @return total value
     */
    public double getTotalEstimatedValue() {
        double total = 0;
        for (HouseHoldItem item : Objects.requireNonNull(houseHoldItems.getValue())) {
            total += item.getPrice();
        }
        return total;
    }
}
