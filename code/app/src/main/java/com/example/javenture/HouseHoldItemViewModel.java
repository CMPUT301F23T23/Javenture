package com.example.javenture;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HouseHoldItemViewModel extends ViewModel {
    private HouseHoldItemRepository itemRepository = new HouseHoldItemRepository();
    private final MutableLiveData<ArrayList<HouseHoldItem>> houseHoldItems = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration listenerRegistration;

    public LiveData<ArrayList<HouseHoldItem>> getHouseHoldItems() {
        return houseHoldItems;
    }

    /**
     * Observe changes to the items collection in the db
     */
    public void observeItems(@Nullable String filterType, @Nullable ArrayList<String> keywords) {
        if (listenerRegistration != null) {
            stopObserveItems();
        }
        listenerRegistration = itemRepository.observeItems(items -> houseHoldItems.postValue(items), filterType, keywords);
    }

    /**
     * Stop observing changes to the items collection in the db
     */
    public void stopObserveItems() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
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
    public void addItem(HouseHoldItem houseHoldItem) {
        itemRepository.addItem(houseHoldItem);
    }

    /**
     * Remove an HouseHoldItem object from the list of houseHoldItems
     * @param item HouseHoldItem object to be removed
     */
    public void deleteItem(HouseHoldItem item) {
        itemRepository.deleteItem(item);
    }

    /**
     * Remove a list of HouseHoldItem objects from the list of houseHoldItems
     * @param items list of HouseHoldItem objects to be removed
     */
    public void deleteItems(List<HouseHoldItem> items) {
        itemRepository.deleteItems(items);
    }

    /**
     * Edit an HouseHoldItem object.
     * The object is identified by its id
     * @param item HouseHoldItem object with updated values
     */
    public void editItem(HouseHoldItem item) {
        itemRepository.editItem(item);
    }

    /**
     * Edit a list of HouseHoldItem objects.
     * The objects are identified by their ids
     * @param items list of HouseHoldItem objects with updated values
     */
    public void editItems(List<HouseHoldItem> items) {
        itemRepository.editItems(items);
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
