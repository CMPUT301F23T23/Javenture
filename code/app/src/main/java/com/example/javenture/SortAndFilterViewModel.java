package com.example.javenture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * This class is used to store the sort and filter options that the user has selected.
 */
public class SortAndFilterViewModel extends ViewModel {
    private final MutableLiveData<SortAndFilterOption> sortAndFilterOption = new MutableLiveData<>(new SortAndFilterOption());

    public LiveData<SortAndFilterOption> getSortAndFilterOption() {
        return sortAndFilterOption;
    }

    public void setSortAndFilterOption(SortAndFilterOption sortAndFilterOption) {
        this.sortAndFilterOption.postValue(sortAndFilterOption);
    }
}
